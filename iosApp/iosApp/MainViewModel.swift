//
//  MainViewModel.swift
//  iosApp
//
//  Created by 여운칠 on 1/26/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared
import CoreLocation


@MainActor
class MainViewModel: ObservableObject {

    let repository:GisMemoRepository
    let locationService = LocationService.service
    let OpenWeatherSdkApiKey = "OpenWeatherSdkApiKey"
    let units = "metric"
    
    @Published var weather:AsyncWeatherInfoState = AsyncWeatherInfoState.Empty()

    init(repository: GisMemoRepository) {
        self.repository = repository
        self.setWeatherInfo()
        self.connectStream()
    }
    
    private func setWeatherInfo() {
        Task{
            do {
                try await repository.setWeatherInfo()
            } catch {
                print(#function, error.localizedDescription )
            }
        }
    }
    
    private func connectStream() {
        Task{
            do {
                try await repository._currentWeatherStateFlow.collect(
                    collector:Collector<AsyncWeatherInfoState> { value in
                        DispatchQueue.main.async {
                            self.weather = value
                        }
                    })
            } catch {
                print(#function, error.localizedDescription )
            }
        }
    }
    
    func getCurrentWeather()  {
        if(self.locationService.authStatus == CLAuthorizationStatus.authorizedAlways
           || self.locationService.authStatus == CLAuthorizationStatus.authorizedWhenInUse){
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
            }
        }
    }
    

    
    
    
}
