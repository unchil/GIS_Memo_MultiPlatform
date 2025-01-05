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


enum LoadableWeather {
    case loading
    case result(String)
    case error(String)
}

@MainActor
class MainViewModel: ObservableObject {

    let repository:GisMemoRepository
    
    @Published var weather:LoadableWeather = LoadableWeather.loading

    init(repository: GisMemoRepository) {
        self.repository = repository
        self.connectStream()
    }
    
     func connectStream() {
         Task{
             do {
                 self.weather = .loading
                 let info = try await repository.getWeatherInfo()
                 self.weather = .result(info)
             }catch{
                 self.weather = .error(error.localizedDescription)
             }
         }
    }
    
}
