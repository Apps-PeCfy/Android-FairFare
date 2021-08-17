package com.fairfareindia.ui.drawer.pojo

class DrawerPojo {
    var id = 0
    var name: String? = null
    var icon = 0

    constructor() {}
    constructor(id: Int, name: String?) {
        this.id = id
        this.name = name
    }

    constructor(id: Int, name: String?, icon: Int) {
        this.id = id
        this.name = name
        this.icon = icon
    }

}