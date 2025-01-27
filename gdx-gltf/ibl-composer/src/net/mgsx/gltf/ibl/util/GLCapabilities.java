package net.mgsx.gltf.ibl.util;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;

public class GLCapabilities {
	public static GLCapabilities i;
	
	public boolean hasMemoryInfo;
	public int maxMemory;
	
	public int maxSizeCubemap8bits;
	public int maxSizeCubemap32bits;
	public int maxSizeFramebuffer;

	public int maxSizeFrameBufferCubeMap;
	
	public GLCapabilities() {
		maxSizeCubemap8bits = GLUtils.getMaxCubemapSize(GL30.GL_RGB8, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE);
		maxSizeCubemap32bits = GLUtils.getMaxCubemapSize(GL30.GL_RGB32F, GL20.GL_RGB, GL20.GL_FLOAT);
		maxSizeFramebuffer = GLUtils.getMaxFrameBufferSize();
		maxSizeFrameBufferCubeMap = GLUtils.getMaxCubemapSize(); // XXX GLUtils.getMaxFrameBufferCubeMapSizeRGB888();
		hasMemoryInfo = GLUtils.hasMemoryInfo();
		if(hasMemoryInfo){
			maxMemory = GLUtils.getMaxMemoryKB();
		}
	}
	
}
