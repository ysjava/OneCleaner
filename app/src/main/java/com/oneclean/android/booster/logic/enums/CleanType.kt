package com.oneclean.android.booster.logic.enums

enum class CleanType(val value: Int){
    CLEAN(0),BOOSTER(1),SAVER(2),COOLER(3),NOTHING(-1);
    companion object{
        fun switchToTypeByValue(value: Int): CleanType {
            return when(value){
                -1 -> NOTHING
                0 -> CLEAN
                1 -> BOOSTER
                2 -> SAVER
                3 -> COOLER
                else -> NOTHING
            }
        }
    }
}