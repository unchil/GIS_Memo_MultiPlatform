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

    var body: some View {
        TestView()
    }
    
    private func TestView() ->AnyView{
        switch viewModel.weather {
        case .loading:
            return AnyView(Text("Loading...").multilineTextAlignment(.center))
        case .error(let description):
            return AnyView(Text(description).multilineTextAlignment(.center))
        case .result(let weather):
            return AnyView(Text(weather).multilineTextAlignment(.center))
        }
    }
    
}

