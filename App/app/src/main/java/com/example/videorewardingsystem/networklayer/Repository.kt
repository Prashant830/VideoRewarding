package com.example.videorewardingsystem.networklayer

import android.graphics.Bitmap
import android.util.Base64
import com.example.videorewardingsystem.networklayer.retrofit.ApiService
import com.example.videorewardingsystem.networklayer.retrofit.PredictionApiService
import com.example.videorewardingsystem.networklayer.retrofit.model.PredictionResponse
import com.example.videorewardingsystem.networklayer.retrofit.model.RewardModel
import com.example.videorewardingsystem.networklayer.retrofit.model.VideoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.ByteArrayOutputStream


class Repository(private val apiService: ApiService , private val predictionApiService: PredictionApiService) : ApiService {

  override suspend fun getVideos(): Response<VideoResponse> {
            return try {
                apiService.getVideos()
            } catch (e: Exception) {
                Response.error(500, ResponseBody.create(null, e.message.toString()))
            }
        }

        override suspend fun sendReward(request: RewardModel): Response<Any> {
            return try {
                apiService.sendReward(request)
            } catch (e: Exception) {
                Response.error(500, ResponseBody.create(null, e.message.toString()))
            }
        }

    suspend fun runPrediction(bitmap: Bitmap): PredictionResponse? {
        return try {
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                val byteArray = outputStream.toByteArray()
                val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

                val body = base64Image.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())

                val response = predictionApiService.runPrediction(
                    url = "chessboarddetect-zfplq/2?api_key=Oa5643F8zd812mOXcJH6",
                    body = body
                )

                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

    }
}
