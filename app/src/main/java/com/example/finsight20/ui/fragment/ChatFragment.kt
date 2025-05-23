package com.example.finsight20.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finsight20.adapter.ChatAdapter
import com.example.finsight20.databinding.FragmentChatBinding
import com.example.finsight20.model.ChatMessage
import com.example.finsight20.ui.viewmodel.ChatBotViewModel
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<ChatMessage>()
    private lateinit var viewModel: ChatBotViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ChatBotViewModel::class.java]

        Log.d("ChatFragment", "ChatFragment açıldı!")

        chatAdapter = ChatAdapter(chatList)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = chatAdapter

        binding.sendButton.setOnClickListener {
            val userMessage = binding.messageEditText.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                chatList.add(ChatMessage(userMessage, true))
                chatAdapter.notifyItemInserted(chatList.size - 1)
                binding.chatRecyclerView.scrollToPosition(chatList.size - 1)
                binding.messageEditText.text.clear()

                lifecycleScope.launch {
                    val typingMessage = ChatMessage("Yazıyor...", false)
                    chatList.add(typingMessage)
                    chatAdapter.notifyItemInserted(chatList.size - 1)
                    binding.chatRecyclerView.scrollToPosition(chatList.size - 1)

                    kotlinx.coroutines.delay(800)

                    val botResponse = viewModel.getBotResponse(userMessage)

                    val lastIndex = chatList.size - 1
                    chatList.removeAt(lastIndex)
                    chatAdapter.notifyItemRemoved(lastIndex)

                    chatList.add(ChatMessage(botResponse, false))
                    chatAdapter.notifyItemInserted(chatList.size - 1)
                    binding.chatRecyclerView.scrollToPosition(chatList.size - 1)
                }
            }
        }

        return binding.root
    }
}