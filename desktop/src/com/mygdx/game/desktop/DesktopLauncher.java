package com.mygdx.game.desktop;


import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import uk.co.electronstudio.ghostjumpers.PimpGameFactory;
import uk.co.electronstudio.retrowar.App;
import uk.co.electronstudio.retrowar.Callback;
import uk.co.electronstudio.retrowar.SimpleApp;
import uk.co.electronstudio.retrowar.utils.SimpleLogger;


public class DesktopLauncher {
	public static void main (String[] arg) {
		DesktopCallback callback = new DesktopCallback();
		App app = new SimpleApp(callback, "Ghost Jumpers", PimpGameFactory.class, new SimpleLogger(), null, true,
				true);
		new LwjglApplication(app, callback.config);
	}

	static class DesktopCallback implements Callback {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		DesktopCallback() {
			config.vSyncEnabled = true;
			config.audioDeviceBufferSize = 1024;
			config.audioDeviceBufferCount = 32;
			config.audioDeviceSimultaneousSources = 32;
			config.useGL30 = true;
			config.gles30ContextMajorVersion = 3;
			config.gles30ContextMinorVersion = 3;

//			config.addIcon("icon256.png", Files.FileType.Internal);
//			config.addIcon("icon128.png", Files.FileType.Internal);
//			config.addIcon("icon64.png", Files.FileType.Internal);
//			config.addIcon("icon32.png", Files.FileType.Internal);
//			config.addIcon("icon16.png", Files.FileType.Internal);
		}


		public void setForegroundFPS(int foregroundFPS) {
			config.foregroundFPS = foregroundFPS;
		}

		public void setBackgroundFPS(int backgroundFPS) {
			config.backgroundFPS = backgroundFPS;
		}

		@Override
		public boolean FPSsupported() {
			return true;
		}
	}

}
