package com.eason.ygosearchcard.ui.home


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eason.ygosearchcard.bean.Card


class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>()
    private val _listData = MutableLiveData<List<Card>>()
    fun setTitle(titleStr :String?){
        _text.value = titleStr
    }


    fun setListData(datas : List<Card>){
        _listData.postValue(datas)
    }
    val cardDatas :LiveData<List<Card>> = _listData

    val text: LiveData<String> = _text

}