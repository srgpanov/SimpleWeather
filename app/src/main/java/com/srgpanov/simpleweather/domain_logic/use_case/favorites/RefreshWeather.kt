package com.srgpanov.simpleweather.domain_logic.use_case.favorites

//class RefreshWeather @Inject constructor(
//    private val repository: DataRepository,
//    private val converter: FavoritesConverter
//) {
//
//    @ExperimentalCoroutinesApi
//    operator fun invoke(): Flow<List<FavoritesViewItem>> = flow {
//        val placesList = repository.getFavoritePlaces().toMutableList()
//        emit(placesList.map(converter::transformFavorite))
//
//        coroutineScope {
//            val deferredList = mutableListOf<Deferred<ResponseResult<CurrentWeatherResponse>>>()
//            for (place in placesList) {
//                val async = async {
//                    repository.getSimpleWeather(place.toGeoPoint())
//                }
//                deferredList.add(async)
//            }
//            val awaitAll = deferredList.awaitAll()
//            awaitAll
//        }
//
//        emit(placesList.map(converter::transformFavorite))
//    }.flowOn(Dispatchers.IO)
//}