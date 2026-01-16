package info.anodsplace.framework.bluetooth

import android.bluetooth.BluetoothClass

enum class BtClassType {
    COMPUTER, PHONE, HEADPHONES, HEADSET
}

val BluetoothClass.classType: BtClassType?
    get() {
        val majorDeviceClass = majorDeviceClass
        if (majorDeviceClass == BluetoothClass.Device.Major.COMPUTER) {
            return BtClassType.COMPUTER
        } else if (majorDeviceClass == BluetoothClass.Device.Major.PHONE) {
            return BtClassType.PHONE
        }

        if (doesClassMatch(PROFILE_A2DP, this)) {
            return BtClassType.HEADPHONES
        }
        return if (doesClassMatch(PROFILE_HEADSET, this)) {
            BtClassType.HEADSET
        } else null
}

private const val PROFILE_HEADSET = 0
private const val PROFILE_A2DP = 1

private fun doesClassMatch(profile: Int, btClass: BluetoothClass): Boolean {
    if (profile == PROFILE_A2DP) {
        if (btClass.hasService(BluetoothClass.Service.RENDER)) {
            return true
        }
        // By the A2DP spec, sinks must indicate the RENDER service.
        // However we found some that do not (Chordette). So lets also
        // match on some other class bits.
        return when (btClass.deviceClass) {
            BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO, BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES, BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER, BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO -> true
            else -> false
        }
    } else if (profile == PROFILE_HEADSET) {
        // The render service class is required by the spec for HFP, so is a
        // pretty good signal
        if (btClass.hasService(BluetoothClass.Service.RENDER)) {
            return true
        }
        // Just in case they forgot the render service class
        return when (btClass.deviceClass) {
            BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE, BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET, BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO -> true
            else -> false
        }
    }
    return false
}
