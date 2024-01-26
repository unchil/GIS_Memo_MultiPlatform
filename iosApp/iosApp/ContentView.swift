import SwiftUI
import shared

struct ContentView: View {
	
    let msg = Greeting().greet()
	var body: some View {
		Text(msg)
	}
}

#Preview {
    ContentView()
}



