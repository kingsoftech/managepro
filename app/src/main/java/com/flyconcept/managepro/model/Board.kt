package com.flyconcept.managepro.model

import android.os.Parcel
import android.os.Parcelable

data class Board(val name:String= "",
                 val image:String = "",
                 val createdBy:String= "",
                 val assignedTo:ArrayList<String> = ArrayList()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!
    ) {
    }

    override fun describeContents(): Int {
        return  0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeString(name)
        dest!!.writeString(image)
        dest!!.writeString(createdBy)
        dest!!.writeStringList(assignedTo)


    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}
