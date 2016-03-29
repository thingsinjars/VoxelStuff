package com.ch.voxel;

import java.util.ArrayList;
import java.util.List;

import com.ch.Model;
import com.ch.SimplexNoise;
import com.ch.Util;
import com.ch.math.Matrix4f;

public class Chunk {

	public static final int CHUNK_SIZE = 64;
	private static final int CHUNK_SIZE_SQUARED = CHUNK_SIZE * CHUNK_SIZE;
	private static final int CHUNK_SIZE_CUBED = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;

	private Block[] blocks;
	public int x, y, z;
	private Model model;
	
	public Model getModel() {
		if (to_gen_model) {
			createModel();
			to_gen_model = false;
		}
		return model;
	}
	
	public Matrix4f getModelMatrix() {
		return new Matrix4f().initTranslation(x * CHUNK_SIZE, y * CHUNK_SIZE, z * CHUNK_SIZE);
	}

	public Chunk(int _x, int _y, int _z) {
		
		this.x = _x;
		this.y = _y;
		this.z = _z;
		
		blocks = new Block[CHUNK_SIZE_CUBED];
		
		for (int i = 0; i < CHUNK_SIZE_CUBED; i++) {
			int z = i / CHUNK_SIZE_SQUARED;
			int ii = i - (z * CHUNK_SIZE_SQUARED);
			int y = ii / CHUNK_SIZE;
			int x = ii % CHUNK_SIZE;
			if (SimplexNoise.noise((x + this.x * CHUNK_SIZE) / 10f, (y + this.y * CHUNK_SIZE) / 10f, (z + this.z * CHUNK_SIZE) / 10f) > 0.1f) {
			blocks[i] = new Block(x, y, z);
			}
		}
	}
	
	

	public void updateBlocks() {
		for (int i = 0; i < CHUNK_SIZE_CUBED; i++) {
			Block b = blocks[i];
				if (b != null) {
				int n_x = i - 1;
				int p_x = i + 1;
				int n_y = i - CHUNK_SIZE;
				int p_y = i + CHUNK_SIZE;
				int n_z = i - CHUNK_SIZE_SQUARED;
				int p_z = i + CHUNK_SIZE_SQUARED;
				
				if (b.x - 1 < 0) {
					//TODO: check neighbor chunk
					b.lt = false;
				} else {
					Block bl = blocks[n_x];
					if (bl == null)
						b.lt = true;
					else
						b.lt = false;
				}
				if (b.y - 1 < 0) {
					//TODO: check neighbor chunk
					b.bt = false;
				} else {
					Block bl = blocks[n_y];
					if (bl == null)
						b.bt = true;
					else
						b.bt = false;
				}
				if (b.z - 1 < 0) {
					//TODO: check neighbor chunk
					b.ft = false;
				} else {
					Block bl = blocks[n_z];
					if (bl == null)
						b.ft = true;
					else
						b.ft = false;
				}
				
				if (b.x + 1 >= CHUNK_SIZE) {
					//TODO: check neighbor chunk
					b.rt = false;
				} else {
					Block bl = blocks[p_x];
					if (bl == null)
						b.rt = true;
					else
						b.rt = false;
				}
				if (b.y + 1 >= CHUNK_SIZE) {
					//TODO: check neighbor chunk
					b.tp = false;
				} else {
					Block bl = blocks[p_y];
					if (bl == null)
						b.tp = true;
					else
						b.tp = false;
				}
				if (b.z + 1 >= CHUNK_SIZE) {
					//TODO: check neighbor chunk
					b.bk = false;
				} else {
					Block bl = blocks[p_z];
					if (bl == null)
						b.bk = true;
					else
						b.bk = false;
				}
			}
		}
	}
	
//	class Vertex3i {
//		
//		public int x, y, z; 
//		public float u, v;
//		
//		public Vertex3i(x, y, z, u, v) {
//			
//		}
//		
//	}
	
	private ArrayList<Float> vertices = new ArrayList<>();
	private ArrayList<Integer> indices = new ArrayList<>();
	private boolean to_gen_model;
	
	
	public void toGenModel() { toGenModel(false); };
	
	public void toGenModel(boolean now) {

		int max_index = 0;
//		System.out.println("gen model");
		for (int i = 0; i < CHUNK_SIZE_CUBED; i++) {
			Block b = blocks[i];
			if (b != null) {
				max_index = gen(vertices, indices, b, max_index);
					
			}
		}
//		System.out.println("vertice   : " + vertices.size() / 5 + " -- floats : " + vertices.size());
//		System.out.println("indices   : " + indices.size());
//		System.out.println("triangles : " + indices.size() / 3);
//		System.out.println("quads     : " + indices.size() / 6);
//		System.out.println("---------------------------\nloading model arrays");
		
		// cant implement filtering and re-indexing for textured cubes
//		{
//			
//			ArrayList<Integer>
//			
//		}
		
//		return Model.load(Util.toFloatArray(new_vertices), Util.toIntArray(new_indices));
		
		if (now) {
			createModel();
			to_gen_model = false;
		} else {
			to_gen_model = true;
		}
		
	}
	
	private void createModel() {
		this.model = Model.load(Util.toFloatArray(vertices), Util.toIntArray(indices));
	}
	
	public Model genModel() {
		
		toGenModel(true);
		
		return this.model;
	}

	private static int gen(List<Float> vertices, List<Integer> indices, Block block, int max_index) {
		
		float x = block.x;
		float y = block.y;
		float z = block.z;
		
		if (block.ft) {
			float[] tmp_v = { //
				x,   y,   z,   0, 0, //
				x+1, y,   z,   1, 0, //
				x+1, y+1, z,   1, 1, //
				x,   y+1, z,   0, 1, //
			}; //
			for (float f : tmp_v) vertices.add(f);
			for (int i : new int[] {0, 1, 2, 0, 2, 3}) indices.add(max_index + i);
			max_index += 4;
		}
		if (block.bk) {
			float[] tmp_v = { //
				x,   y,   z+1,   1, 0, //
				x+1, y,   z+1,   0, 0, //
				x+1, y+1, z+1,   0, 1, //
				x,   y+1, z+1,   1, 1, //
			}; //
			for (float f : tmp_v) vertices.add(f);
			for (int i : new int[] {0, 3, 2, 0, 2, 1}) indices.add(max_index + i);
			max_index += 4;
		}
		if (block.bt) {
			float[] tmp_v = { //
				x,   y,   z,     0, 0, //
				x+1, y,   z,   	 1, 0, //
				x+1, y,   z+1,   1, 1, //
				x,   y,   z+1,   0, 1, //
			}; //
			for (float f : tmp_v) vertices.add(f);
			for (int i : new int[] {0, 3, 2, 0, 2, 1}) indices.add(max_index + i);
			max_index += 4;
		}
		if (block.tp) {
			float[] tmp_v = { //
				x,   y+1, z,     0, 0, //
				x+1, y+1, z,     1, 0, //
				x+1, y+1, z+1,   1, 1, //
				x,   y+1, z+1,   0, 1, //
			}; //
			for (float f : tmp_v) vertices.add(f);
			for (int i : new int[] {0, 1, 2, 0, 2, 3}) indices.add(max_index + i);
			max_index += 4;
		}
		if (block.lt) {
			float[] tmp_v = { //
				x,   y,   z,     1, 0, //
				x,   y+1, z,     1, 1, //
				x,   y+1, z+1,   0, 1, //
				x,   y,   z+1,   0, 0, //
			}; //
			for (float f : tmp_v) vertices.add(f);
			for (int i : new int[] {0, 1, 2, 0, 2, 3}) indices.add(max_index + i);
			max_index += 4;
		}
		if (block.rt) {
			float[] tmp_v = { //
				x+1, y,   z,     0, 0, //
				x+1, y+1, z,     0, 1, //
				x+1, y+1, z+1,   1, 1, //
				x+1, y,   z+1,   1, 0, //
			}; //
			for (float f : tmp_v) vertices.add(f);
			for (int i : new int[] {0, 3, 2, 0, 2, 1}) indices.add(max_index + i);
			max_index += 4;
		}
		return max_index;
	}

}
