package com.eason.ygosearchcard.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.eason.ygosearchcard.adapter.CardAdapter
import com.eason.ygosearchcard.bean.Card
import com.eason.ygosearchcard.databinding.FragmentHomeBinding
import kotlinx.coroutines.*
import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.rlvCard.layoutManager = LinearLayoutManager(context)
        viewModel.cardDatas.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                return@observe
            }


            val cardAdapter = CardAdapter(context,it)
            binding.rlvCard.adapter = cardAdapter

        }

        viewModel.text.observe(viewLifecycleOwner) {str ->
            //togetKPdata
            GlobalScope.launch {
                    toGetData(str)
            }

        }
        return root
    }

    private  fun toGetData(it: String?) {
        if (it.isNullOrBlank()){
            return
        }
            val client = OkHttpClient()
            val request = Request.Builder().url("https://ygocdb.com/?search=$it")
                .build()
            val call = client.newCall(request)
            var cards = ArrayList<Card>()
         call.enqueue(object : Callback{
                 override  fun  onResponse(call: Call, response: Response) {
                    if (response.code==200){
                        val htmlStr = response.body!!.string()
                        val document: Document = Jsoup.parse(htmlStr)
                        val searchResult: List<Element>? = document.select("div.card")

                        if (searchResult != null) {
                            for (index in searchResult.indices){
                                val cardInfo: Card = Card(
                                    img = searchResult[index].select("img")[0].dataset()["original"].toString(),
                                    desc = searchResult[index].select("div.desc").html()
                                        .replace(
                                            Regex("</*?div.*?>|</*?strong.*?>|</*?a.*?>|</*?span.*?>|</*?br>",
                                                RegexOption.IGNORE_CASE),
                                            ""
                                        ).replace(
                                            Regex("</*?hr>", RegexOption.IGNORE_CASE), "\n"
                                        )
                                )
                                cards.add(cardInfo)
                            }
                        }
                        viewModel.setListData(datas = cards)
                    }


                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.d("TAGGGGGGGGG",e.message!!)
                }
            })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}