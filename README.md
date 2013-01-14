Java Server and android client for controlling Windows, OS X and linux audio device volume.

The server basically calls Terminal commands to adjust the host volume, this is some-what limited, but it seems to work!

The Windows volume controller requires the use of Ray M.'s CoreAudioApi library. I created a simple wrapper to allow
the server to adjust the volume via command prompt.

(http://www.codeproject.com/Articles/18520/Vista-Core-Audio-API-Master-Volume-Control)

The CoreAudioApi requires .Net 4.0 or higher
