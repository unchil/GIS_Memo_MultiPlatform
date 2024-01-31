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
import UIKit


@MainActor
class MainViewModel: ObservableObject {

    let repository:GisMemoRepository
    let locationService = LocationService.service
    let OpenWeatherSdkApiKey = "OpenWeatherSdkApiKey"
    let units = "metric"
    var getWeatherInfoTask:Task<Void, Never>?
    


    
    @Published var weather:AsyncWeatherInfoState = AsyncWeatherInfoState.Empty()

    init(repository: GisMemoRepository) {
        self.repository = repository
        self.connectStream()
    }
    
    func setWeatherInfo() async {
        do {
            try await repository.setWeatherInfo()
        } catch {
            print(#function, error.localizedDescription )
        }
    }
    
     func connectStream() {
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
                
                guard self.getWeatherInfoTask == nil else { return }
                
                self.getWeatherInfoTask = Task{
                    do{
                        try await self.repository.getWeatherData(
                            lat: location.coordinate.latitude.description,
                            lon: location.coordinate.longitude.description,
                            appid: self.OpenWeatherSdkApiKey,
                            units: self.units)
                    }catch {
                        print(#function, error.localizedDescription)
                    }
                    self.getWeatherInfoTask = nil
                }
            }
        }
    }
    

    
    
    
}
