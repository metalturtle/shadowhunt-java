package gdxgraphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import util.ConfigManager;

public class GdxShaderManager {
	ArrayList <ShaderProgram> shaders;
	HashMap <String, Integer> shaderMap;
	String VERTEX_SHADER;
	public GdxShaderManager(ConfigManager ConfigManage) {
		shaders = new ArrayList <ShaderProgram>();
		shaderMap = new HashMap<String,Integer>();
		ShaderProgram.pedantic = true;
		
		HashMap<Object,Object> shaderConfigMap = ConfigManage.get_object("resource", "shader");
		Set shaderSet = shaderConfigMap.keySet();
		String folder = (String)shaderConfigMap.get("folder");
		int k = 0;
		VERTEX_SHADER = Gdx.files.internal(folder+"//"+(String)shaderConfigMap.get("vertex_shader")).readString();
		for (Object shaderObjName : shaderSet) {
			String shaderName = (String)shaderObjName;
			if (shaderName.equals("folder") || shaderName.equals("vertex_shader")) {
				continue;
			}
			String shaderFile = (String)shaderConfigMap.get(shaderName);
			ShaderProgram shader = new ShaderProgram(
					VERTEX_SHADER,
					 Gdx.files.internal(folder+"//"+shaderFile).readString()
					);
			shaders.add(shader);
			shaderMap.put(shaderName, k);
			k+=1;
		}

	}
	
	public ShaderProgram get_shader(String name) {
		return shaders.get(shaderMap.get(name));
	}
	
	public ShaderProgram get_shader(int id) {
		return shaders.get(id);
	}
	
	public int get_shader_id(String name) {
		return shaderMap.get(name);
	}
	
	
	public void dispose() {
		for(int i = 0; i < shaders.size(); i++) {
			shaders.get(i).dispose();
		}
	}
}
