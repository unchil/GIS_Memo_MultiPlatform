//
//  LocationService.swift
//  iosApp
//
//  Created by 여운칠 on 1/26/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import CoreLocation

class LocationService: NSObject, CLLocationManagerDelegate {

    static let service = LocationService()

    @Published var authStatus:CLAuthorizationStatus = .notDetermined

    let cLLocationManager:CLLocationManager
    var completionHandler: ((CLLocation) -> (Void))?

    private override init() {
        self.cLLocationManager =  CLLocationManager()
        super.init()
        self.cLLocationManager.delegate = self
        self.cLLocationManager.desiredAccuracy = kCLLocationAccuracyBest
        self.cLLocationManager.requestWhenInUseAuthorization()
    }

    func getCurrentLocation( completion: @escaping (CLLocation) -> ()?)  {

        let sleepTime:TimeInterval = 0.5
        var location:CLLocation?

        while( location == nil ) {
            location = self.cLLocationManager.location
            Thread.sleep(forTimeInterval: sleepTime )
        }
        completion(location!)
    }


    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        self.authStatus = status
        switch status {
            case .notDetermined, .restricted, .denied:
                self.cLLocationManager.requestWhenInUseAuthorization()
            case .authorizedAlways, .authorizedWhenInUse: do {
                self.cLLocationManager.startUpdatingLocation()
            }
            @unknown default:
                self.cLLocationManager.requestWhenInUseAuthorization()
        }
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        print(#function, "latitude: " + (locations.last?.coordinate.latitude.description ?? "" +
                         ", longitude: " + (locations.last?.coordinate.longitude.description ?? "")))

    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
     //  print(#function, error.localizedDescription)
    }
}

