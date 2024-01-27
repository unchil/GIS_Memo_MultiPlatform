import SwiftUI
import shared

@main
struct iOSApp: App {

    let repository = GisMemoRepository(databaseDriverFactory: DatabaseDriverFactory())
    
	var body: some Scene {
		WindowGroup {
            WeatherView(viewModel: .init(repository: repository))
		}
	}
}
