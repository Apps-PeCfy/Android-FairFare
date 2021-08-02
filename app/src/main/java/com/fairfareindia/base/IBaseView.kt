package com.fairfareindia.base

interface IBaseView {

    fun showWait()
    fun removeWait()
    fun onFailure(appErrorMessage: String?)
}