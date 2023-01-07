package resource;

public class TextureNameMap {
	byte type;
	private int tex_id;
	public TextureNameMap(byte type, int tex_id) {
		this.type = type;
		this.set_tex_id(tex_id);
	}
	public int get_tex_id() {
		return tex_id;
	}
	public void set_tex_id(int tex_id) {
		this.tex_id = tex_id;
	}
}
