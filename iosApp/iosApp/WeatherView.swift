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
    @State var weatherInfo:String = /*@START_MENU_TOKEN@*/"Hello, World!"/*@END_MENU_TOKEN@*/
    
    var body: some View {
        
        Text(self.weatherInfo)
            .onAppear{
                viewModel.getCurrentWeather()
            }.onChange(of: viewModel.weather){
                if let result = viewModel.weather as? AsyncWeatherInfoState.Success{
                    self.weatherInfo = result.data.description()
                }
            }
    }
}

