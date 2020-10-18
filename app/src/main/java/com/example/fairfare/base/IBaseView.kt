package com.example.fairfare.base

interface IBaseView {

    fun showWait()
    fun removeWait()
    fun onFailure(appErrorMessage: String?)
}