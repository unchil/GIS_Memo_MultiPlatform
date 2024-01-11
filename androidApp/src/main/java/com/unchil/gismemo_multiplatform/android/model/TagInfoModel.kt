package com.unchil.gismemo_multiplatform.android.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Cabin
import androidx.compose.material.icons.outlined.DownhillSkiing
import androidx.compose.material.icons.outlined.EvStation
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.FlightLand
import androidx.compose.material.icons.outlined.FlightTakeoff
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.Hiking
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.material.icons.outlined.House
import androidx.compose.material.icons.outlined.Kayaking
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.LocalTaxi
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.RollerSkating
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ScubaDiving
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Skateboarding
import androidx.compose.material.icons.outlined.Snowboarding
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.Theaters
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import com.unchil.gismemo_multiplatform.android.R


data class TagInfoData(
    var icon : ImageVector,
    var name: Int,
    var isSet: MutableState<Boolean> = mutableStateOf(false)
)

val TagInfoDataList: List<TagInfoData> = listOf(
    TagInfoData(Icons.Outlined.ShoppingCart, R.string.search_hashTag_ShoppingCart),
    TagInfoData(Icons.Outlined.AccountBalance, R.string.search_hashTag_AccountBalance),
    TagInfoData(Icons.Outlined.Store, R.string.search_hashTag_Store),
    TagInfoData(Icons.Outlined.Theaters, R.string.search_hashTag_Theaters),
    TagInfoData(Icons.Outlined.FlightTakeoff, R.string.search_hashTag_FlightTakeoff),
    TagInfoData(Icons.Outlined.FlightLand, R.string.search_hashTag_FlightLand),
    TagInfoData(Icons.Outlined.Hotel, R.string.search_hashTag_Hotel),
    TagInfoData(Icons.Outlined.School, R.string.search_hashTag_School),
    TagInfoData(Icons.Outlined.Hiking, R.string.search_hashTag_Hiking),
    TagInfoData(Icons.Outlined.DownhillSkiing, R.string.search_hashTag_DownhillSkiing),
    TagInfoData(Icons.Outlined.Kayaking, R.string.search_hashTag_Kayaking),
    TagInfoData(Icons.Outlined.Skateboarding, R.string.search_hashTag_Skateboarding),
    TagInfoData(Icons.Outlined.Snowboarding, R.string.search_hashTag_Snowboarding),
    TagInfoData(Icons.Outlined.ScubaDiving, R.string.search_hashTag_ScubaDiving),
    TagInfoData(Icons.Outlined.RollerSkating, R.string.search_hashTag_RollerSkating),
    TagInfoData(Icons.Outlined.Photo, R.string.search_hashTag_Photo),
    TagInfoData(Icons.Outlined.Restaurant, R.string.search_hashTag_Restaurant),
    TagInfoData(Icons.Outlined.Park, R.string.search_hashTag_Park),
    TagInfoData(Icons.Outlined.LocalCafe, R.string.search_hashTag_LocalCafe),
    TagInfoData(Icons.Outlined.LocalTaxi, R.string.search_hashTag_LocalTaxi),
    TagInfoData(Icons.Outlined.Forest, R.string.search_hashTag_Forest),
    TagInfoData(Icons.Outlined.EvStation, R.string.search_hashTag_EvStation),
    TagInfoData(Icons.Outlined.FitnessCenter, R.string.search_hashTag_FitnessCenter),
    TagInfoData(Icons.Outlined.House, R.string.search_hashTag_House),
    TagInfoData(Icons.Outlined.Apartment, R.string.search_hashTag_Apartment),
    TagInfoData(Icons.Outlined.Cabin, R.string.search_hashTag_Cabin)
).sortedBy {
    it.name
}

fun  List<TagInfoData>.clear(){
    this.forEach {
        it.isSet.value = false
    }
}
