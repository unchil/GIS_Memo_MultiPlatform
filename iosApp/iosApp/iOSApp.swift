import SwiftUI
import shared

@main
struct iOSApp: App {

    let repository = PlatformObject().platform.getRepository(context: nil)
    
	var body: some Scene {
		WindowGroup {
            WeatherView(viewModel: .init(repository: repository!))
		}
	}
}
