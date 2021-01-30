package com.example.fairfare.ui.home.pojo

class PickUpLocationModel {
     var latitude : Double? = 0.0
     var longitude : Double? = 0.0
     var isSource : Boolean? = false
     var address : String? = ""
     var keyAirport : String? = ""

     constructor(latitude: Double?, longitude: Double?, isSource : Boolean?, address : String?, keyAirport : String?) {
          this.latitude = latitude
          this.longitude = longitude
          this.isSource = isSource
          this.address = address
          this.keyAirport = keyAirport
     }
}