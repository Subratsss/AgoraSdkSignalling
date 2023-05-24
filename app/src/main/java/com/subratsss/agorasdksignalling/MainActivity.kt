package com.subratsss.agorasdksignalling

import android.R
import android.R.id
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.subratsss.agorasdksignalling.databinding.ActivityMainBinding
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.rtm.RtmChannel
import io.agora.rtm.RtmChannelAttribute
import io.agora.rtm.RtmChannelListener
import io.agora.rtm.RtmChannelMember
import io.agora.rtm.RtmClient
import io.agora.rtm.RtmClientListener
import io.agora.rtm.RtmFileMessage
import io.agora.rtm.RtmImageMessage
import io.agora.rtm.RtmMediaOperationProgress
import io.agora.rtm.RtmMessage
import io.agora.rtm.SendMessageOptions


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private var uid: String? = null
    private var channel_name: String? = null
    private var AppID: String = "bdbd981e811747f8869d362bef6b4294"
    private var mRtmClient: RtmClient? = null
    private var mRtmChannel: RtmChannel? = null
    private var peer_id: String? = null
    private var message_content: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        try {

            mRtmClient = RtmClient.createInstance(baseContext, AppID, object : RtmClientListener {
                override fun onConnectionStateChanged(state: Int, reason: Int) {
                    val text = "Connection state changed to $state" + "Reason: " + reason
                    writeToMessageHistory(text)
                }

                override fun onMessageReceived(rtmMessage: RtmMessage?, peerId: String?) {
                    val text =
                        "Message received from $peerId Message: ${rtmMessage?.text.toString()}"
                    writeToMessageHistory(text)
                }

                override fun onImageMessageReceivedFromPeer(p0: RtmImageMessage?, p1: String?) {
                }

                override fun onFileMessageReceivedFromPeer(p0: RtmFileMessage?, p1: String?) {
                }

                override fun onMediaUploadingProgress(p0: RtmMediaOperationProgress?, p1: Long) {
                }

                override fun onMediaDownloadingProgress(p0: RtmMediaOperationProgress?, p1: Long) {
                }

                override fun onTokenExpired() {
                }

                override fun onTokenPrivilegeWillExpire() {
                }

                override fun onPeersOnlineStatusChanged(p0: MutableMap<String, Int>?) {
                }

            })
        } catch (e: Exception) {
            throw RuntimeException("initialization failed!")
        }
    }

    fun onClickLogin(view: View) {
        val token = ""
        uid = binding.uid.text.toString()
        // Log in to Signaling
        mRtmClient!!.login(token, uid, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {}
            override fun onFailure(errorInfo: ErrorInfo) {
                val text: CharSequence = "User: $uid failed to log in to Signaling!$errorInfo"
                runOnUiThread {
                    Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun onClickJoin(view: View) {
        channel_name = binding.channelName.text.toString()
// Create a channel listener
        val mRtmChannelListener: RtmChannelListener = object : RtmChannelListener {
            override fun onMemberCountUpdated(p0: Int) {

            }

            override fun onAttributesUpdated(p0: MutableList<RtmChannelAttribute>?) {

            }

            override fun onMessageReceived(message: RtmMessage?, fromMember: RtmChannelMember?) {
                val message_text = "Message received from ${fromMember?.userId}:${message?.text}"
                writeToMessageHistory(message_text)
            }

            override fun onImageMessageReceived(p0: RtmImageMessage?, p1: RtmChannelMember?) {

            }

            override fun onFileMessageReceived(p0: RtmFileMessage?, p1: RtmChannelMember?) {

            }

            override fun onMemberJoined(p0: RtmChannelMember?) {

            }

            override fun onMemberLeft(p0: RtmChannelMember?) {

            }

        }
        try {
            mRtmChannel = mRtmClient?.createChannel(channel_name, mRtmChannelListener)
        } catch (e: RuntimeException) {

        }

        mRtmChannel?.join(object : ResultCallback<Void?> {
            override fun onSuccess(p0: Void?) {

            }

            override fun onFailure(errorInfo: ErrorInfo?) {
                val text: CharSequence =
                    "User: " + uid + " failed to join the channel!" + errorInfo.toString()
                runOnUiThread {
                    Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    // Button to log out of Signaling
    fun onClickLogout(view: View) {
        // Log out of Signaling
        mRtmClient?.logout(null)
    }

    fun onClickLeave(view: View) {
        mRtmChannel?.leave(null)
    }

    fun onClickSendPeerMsg(view: View) {
        val message: RtmMessage? = mRtmClient?.createMessage()
        message?.text = binding.msgBox.text.toString()

        peer_id = binding.peerName.text.toString()

        val option: SendMessageOptions = SendMessageOptions()
        option.enableOfflineMessaging = true

        mRtmClient?.sendMessageToPeer(peer_id, message, option, object : ResultCallback<Void> {
            override fun onSuccess(p0: Void?) {
                val text = "Message sent from   $uid  To   $peer_id  ：${message?.text}"
                writeToMessageHistory(text)
            }

            override fun onFailure(errorInfo: ErrorInfo?) {
                val text = "Message fails to send from   $uid  To   $peer_id  ：Error $errorInfo"
                writeToMessageHistory(text)
            }

        })
    }

    fun onClickSendChannelMsg(view: View) {
        val message_context = binding.msgBox.text.toString()
        val message: RtmMessage? = mRtmClient?.createMessage()
        message?.text = message_context

        mRtmChannel?.sendMessage(message,object:ResultCallback<Void>{
            override fun onSuccess(p0: Void?) {
                val text =
                   "Message sent to channel ${mRtmChannel!!.id} : ${message?.text}"
                writeToMessageHistory(text)
            }

            override fun onFailure(errorInfo: ErrorInfo?) {
                val text =
                    "Message fails to send to channel ${mRtmChannel!!.id} Error: $errorInfo"
                writeToMessageHistory(text)
            }

        })
    }

    fun writeToMessageHistory(record: String) {
        binding.messageHistory.append(record)
    }
}