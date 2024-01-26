//
//  MainViewModel.swift
//  iosApp
//
//  Created by 여운칠 on 1/26/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared


/*
enum AsyncWeatherInfoState {
    case loading
    case empty
    case success(CURRENTWEATHER_TBL)
    case error(String)
}
 */


@MainActor
class MainViewModel: ObservableObject {

    let repository:GisMemoRepository
    let locationService = LocationService.service
    let OpenWeatherSdkApiKey = "OpenWeatherSdkApiKey"
    let units = "metric"
    
    @Published var weather:AsyncWeatherInfoState?

    
    init(repository: GisMemoRepository) {
        self.repository = repository
        self.connectWeatherInfoStream()
    
    }
    
    func connectWeatherInfoStream(){
        Task{
            do{
                try await repository.setWeatherInfo()
            }catch{
                print(#function, error.localizedDescription)
            }
        }

        Task {
            do {
 
                try await repository._currentWeatherStateFlow.collect(
                collector:Collector<AsyncWeatherInfoState> { value in
                    self.weather = value
                })
       

                
            } catch {
              print("currentWeatherStateFlow", error )
            }
        }


    }
    
    
    func getCurrentWeather()  {
        switch self.locationService.authStatus {
            case .notDetermined, .restricted, .denied: do {}
            case .authorizedAlways, .authorizedWhenInUse: do {
                self.locationService.getCurrentLocation { location in
                    Task{
                        do{
                            try await self.repository.getWeatherData(
                                lat: location.coordinate.latitude.description,
                                lon: location.coordinate.longitude.description,
                                appid: self.OpenWeatherSdkApiKey,
                                units: self.units)
                        }catch {
                            print(#function, error.localizedDescription)
                        }
                    }
                }}
            @unknown default: do {}
        }
    }
    
    
    
    
    
}
