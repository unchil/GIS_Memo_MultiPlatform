import SwiftUI
import shared


struct ContentView: View {
	
    let msg = Greeting().greet()
	var body: some View {
		Text(msg)
	}
}


struct Content_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
