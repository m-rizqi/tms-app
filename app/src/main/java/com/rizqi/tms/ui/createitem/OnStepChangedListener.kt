package com.rizqi.tms.ui.createitem

interface OnStepChangedListener {
    fun changeToStep(nextStep : Int)
    fun <T> onJourneyFinished(data : T)
}