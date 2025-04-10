
## 📞 Java VoIP Project

This project allows for real-time VoIP (Voice over IP) communication between two devices, as well as playback of a `.wav` audio file.

---

### 🛠️ How to Compile

From the **root directory** of the project, run:

```bash
javac client/*.java
```

This will compile all necessary client-side Java files.

---

### 📲 How to Make a Call

To start a VoIP call:

```bash
java client.Call
```

You will be prompted to enter the IP address of the other party.

Make sure:
- The other side is also running `Call.java`
- Your firewalls allow UDP traffic on ports `50005` and `50006`
- You switch the port numbers on the two sides

---

### 🔊 How to Play a WAV File

To play a sample audio file (`song.wav` must be in the same directory as `VoiceNote.java`):

```bash
java client.VoiceNote
```

This plays the audio using a specific output device (you can choose one from a list).

---

### 📁 Project Structure

```
project-root/
├── client/
│   ├── Call.java
│   ├── Voip.java
│   ├── VoiceNote.java
│   └── [compiled .class files]
├── song.wav
```
