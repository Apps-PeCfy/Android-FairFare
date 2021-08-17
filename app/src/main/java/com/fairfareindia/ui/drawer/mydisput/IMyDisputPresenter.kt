package com.fairfareindia.ui.drawer.mydisput

interface IMyDisputPresenter {
    fun getMyDisput( token: String?)
    fun deleteDisput( token: String?,id: String?)
    fun fileComplaint( token: String?,id: String?)

}