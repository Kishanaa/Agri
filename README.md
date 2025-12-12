# 🌳 ForestGuard
A two-part elephant detection and forest safety system used by villagers and the Forest Department.

---

## 📱 ForestGuard — Android Application

Users can access the app **only after entering the correct pincode: `474006`**.  
After entering the pincode, they choose **Forest Department** or **Villager**, then sign in using **Google**.

### Features
- View detected **elephant images** and **locations** by selecting a date
- Enter phone number and **send SMS alerts**
- **Change language** anytime
- **Graphs & Analytics**
  - **24-hour line chart** (hourly elephant detection)
  - **Monthly heatmap** (detection frequency)
- **Report Elephant**: users can report live elephant sightings
- **See Reports**: Forest Department can view submitted reports and take action

---

## 💻 forest_guard — Flutter Web Application

Designed for **Forest Department officials**.

### Features
- **24-hour elephant detection line chart**
- **Monthly heatmap** showing detection frequency
- **Map view** showing the **latest elephant detection location**

## 🐘 Raspberry Pi Elephant Detection System (YOLO + ESP32 + LoRa)

This system uses a **Raspberry Pi**, **ESP32**, **YOLO model**, **PIR motion sensor**, **ADS1115 microphone**, and **LDR sensor** to detect elephants and send alerts to the Forest Department.

---

### 🔧 How the Detection Logic Works

The Raspberry Pi monitors three inputs:

1. **PIR Motion Sensor**
2. **LDR Sensor** (Night / Day detection)
3. **Microphone (Sound Level)**

#### **Final Logic Flow**
- **MOTION + DARK** → send `{"event":"MOTION","ldr":1}` to ESP32 → run detection  
- **MOTION + BRIGHT** → send `{"event":"MOTION","ldr":0}` to ESP32 → run detection  
- **LOUD SOUND** → run detection only  
- **ELEPHANT FOUND** → send elephant alert JSON to ESP32 via LoRa + play alternating alert sounds  

---

## 🎥 YOLO-Based Elephant Detection

- Uses custom YOLO model: `animal_detector_v1_best.pt`
- Camera frames are processed in real-time.
- Elephant must appear **in at least 4 frames** (3-frame verification rule).
- Once an elephant is confirmed:
  - GPS coordinates are read
  - LDR light state is added
  - JSON alert is sent to ESP32 (LoRa transmission)
  - Alarm sound plays (alternates between two audio files)

---

## 📡 LoRa Communication (Raspberry Pi → ESP32)

When an elephant is detected and verified, the Raspberry Pi sends a JSON message like:

```json
{
  "event": "ELEPHANT",
  "lat": 26.20108,
  "lon": 78.22689,
  "ldr": 1,
  "time": 1733992000
}
