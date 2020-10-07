/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.anodsplace.framework.os

import android.os.Build
import android.os.Handler
import android.os.Looper

import java.lang.reflect.InvocationTargetException
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class TaskExecutor {

    private val mLock = Any()

    private val mDiskIO = Executors.newFixedThreadPool(4, object : ThreadFactory {
        private val THREAD_NAME_STEM = "watcher_disk_io_%d"

        private val mThreadId = AtomicInteger(0)

        override fun newThread(r: Runnable): Thread {
            val t = Thread(r)
            t.name = String.format(THREAD_NAME_STEM, mThreadId.getAndIncrement())
            return t
        }
    })

    @Volatile
    private var mMainHandler: Handler? = null

    val isMainThread: Boolean
        get() = Looper.getMainLooper().thread === Thread.currentThread()

    fun executeOnDiskIO(runnable: Runnable) {
        mDiskIO.execute(runnable)
    }

    fun postToMainThread(runnable: Runnable) {
        if (mMainHandler == null) {
            synchronized(mLock) {
                if (mMainHandler == null) {
                    mMainHandler = createAsync(Looper.getMainLooper())
                }
            }
        }

        mMainHandler!!.post(runnable)
    }

    private fun createAsync(looper: Looper): Handler {
        if (Build.VERSION.SDK_INT >= 28) {
            return Handler.createAsync(looper)
        }
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                return Handler::class.java.getDeclaredConstructor(
                            Looper::class.java,
                            Handler.Callback::class.java,
                            Boolean::class.javaPrimitiveType)
                        .newInstance(looper, null, true)
            } catch (ignored: IllegalAccessException) {
            } catch (ignored: InstantiationException) {
            } catch (ignored: NoSuchMethodException) {
            } catch (e: InvocationTargetException) {
                return Handler(looper)
            }
        }
        return Handler(looper)
    }
}
