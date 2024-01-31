//
//  WeatherView.swift
//  iosApp
//
//  Created by 여운칠 on 1/26/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared

@available(iOS 17.0, *)
struct WeatherView: View {
    
    @ObservedObject private(set) var viewModel: MainViewModel
    @State var weatherInfo:String = "Beautiful, World!"
    
    var body: some View {
        
        Text(self.weatherInfo)
        .task {
            await viewModel.setWeatherInfo()
        }
        .onAppear{
            self.viewModel.getCurrentWeather()
        }.onChange(of: viewModel.weather){
            
            if let result = viewModel.weather as? AsyncWeatherInfoState.Success{
                self.weatherInfo = result.data.description()
            }
            
        }
    }
}

