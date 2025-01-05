//
//  Collecter.swift
//  iosApp
//
//  Created by 여운칠 on 1/26/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

class Collector<T> : Kotlinx_coroutines_coreFlowCollector {
    let callback:(T) -> Void
    
    init(callback: @escaping (T) -> Void) {
        self.callback = callback
    }
     
    func emit(value: Any?, completionHandler: @escaping (Error?) -> Void) {
        callback(value as! T)
        completionHandler(nil)
    }

}



class MemoPagingDataCollector: Kotlinx_coroutines_coreFlowCollector {

    private let pagingCollectionViewController: Paging_runtime_uikitPagingCollectionViewController<MEMO_TBL>

  init(pagingCollectionViewController: Paging_runtime_uikitPagingCollectionViewController<MEMO_TBL>) {
    self.pagingCollectionViewController = pagingCollectionViewController
  }

  func emit(value: Any?, completionHandler: @escaping (Error?) -> Void) {
    let pagingData = value as! Paging_commonPagingData<MEMO_TBL>
    DispatchQueue.main.async {
      self.pagingCollectionViewController.submitData(pagingData: pagingData, completionHandler: {_ in print("completed MemoPagingDataCollector")})
    }
  }
}

/*
 class ViewModelCollector: Kotlinx_coroutines_coreFlowCollector {
   
   private let pagingCollectionViewController: Paging_runtime_uikitPagingCollectionViewController<Repository>
   
   init(pagingCollectionViewController: Paging_runtime_uikitPagingCollectionViewController<Repository>) {
     self.pagingCollectionViewController = pagingCollectionViewController
   }
   
   func emit(value: Any?, completionHandler: @escaping (Error?) -> Void) {
     switch (value as! ViewModel) {
       case is ViewModelSearchResults:
         let viewModel = value as! ViewModelSearchResults
         DispatchQueue.main.async {
           viewModel.repositories.collect(collector: PagingDataCollector(pagingCollectionViewController: self.pagingCollectionViewController), completionHandler: {_ in print("completed ViewModelCollector")})
         }
       default:
         print("Unsupported ViewModel:", value)
     }
   }
 }
 */
