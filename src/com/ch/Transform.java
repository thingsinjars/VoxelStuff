package com.ch;

import com.ch.math.Matrix4f;
import com.ch.math.Quaternion;
import com.ch.math.Vector3f;

/**
 * from the given Java file is a class that represents a transformation in a 3D space.
 * It has several properties and methods for rotating and scaling a transform, as
 * well as updating its position and rotation based on user input. The class also
 * provides methods for getting and setting the transform's parent, position, rotation,
 * and scale.
 */
public class Transform {

	private Transform parent;
	private Matrix4f parentMatrix;

	private Vector3f pos;
	private Quaternion rot;
	private Vector3f scale;

	private Vector3f oldPos;
	private Quaternion oldRot;
	private Vector3f oldScale;

	public Transform() {
		pos = new Vector3f(0, 0, 0);
		rot = new Quaternion(1, 0, 0, 0);
		scale = new Vector3f(1, 1, 1);
		
		oldPos = new Vector3f(0, 0, 0);
		oldRot = new Quaternion(1, 0, 0, 0);
		oldScale = new Vector3f(1, 1, 1);

		parentMatrix = new Matrix4f().initIdentity();
	}

	/**
	 * updates the values of its member variables `pos`, `rot`, and `scale`. If any of
	 * these values have changed since the last update, they are set to the new values.
	 * Otherwise, new instances of these classes are created with the current values.
	 */
	public void update() {
		if (oldPos != null) {
			if (!oldPos.equals(pos))
				oldPos.set(pos);
			if (!oldRot.equals(rot))
				oldRot.set(rot);
			if (!oldScale.equals(scale))
				oldScale.set(scale);
		} else {
			oldPos = new Vector3f().set(pos);
			oldRot = new Quaternion().set(rot);
			oldScale = new Vector3f().set(scale);
		}
	}

	/**
	 * multiplies a quaternion representing a rotation axis by an angle and normalizes
	 * the result, producing another quaternion that represents the rotated orientation.
	 * 
	 * @param axis 3D rotation axis around which the rotation will occur.
	 * 
	 * 	- `axis` is a `Vector3f` object representing a 3D vector with x, y, and z components.
	 * 	- The `x`, `y`, and `z` components of `axis` are used to define the rotation axis.
	 * 	- The `angle` parameter represents the angle of rotation in radians.
	 * 
	 * @param angle 3D rotation angle of the object around the specified axis.
	 */
	public void rotate(Vector3f axis, float angle) {
		rot = new Quaternion(axis, angle).mul(rot).normalized();
	}

	/**
	 * computes the rotation needed to look at a point and an up vector in 3D space.
	 * 
	 * @param point 3D position that the lookAt rotation should be applied to.
	 * 
	 * 	- `Vector3f point`: Represents a 3D point in space with x, y, and z components.
	 * 	- `up`: Represents a vector pointing upwards in the 3D space, with a direction
	 * that is perpendicular to the plane of the point.
	 * 
	 * @param up 3D direction along which the object will look at the point passed as argument.
	 * 
	 * 	- The `Vector3f` class represents a three-dimensional vector in mathematics.
	 * 	- The `getLookAtRotation` method returns a rotation matrix that aligns the `rot`
	 * object with the specified `point` and `up` vectors.
	 */
	public void lookAt(Vector3f point, Vector3f up) {
		rot = getLookAtRotation(point, up);
	}

	/**
	 * computes a rotation quaternion that looks at a specified point from a given up
	 * vector, using the rotation matrix representation.
	 * 
	 * @param point 3D position in space that the look-at rotation is to be applied around.
	 * 
	 * 	- `point` is a `Vector3f` object representing a 3D point in space.
	 * 	- `up` is another `Vector3f` object representing a reference direction in space.
	 * 
	 * @param up 3D vector that defines the orientation of the look-at axis, which is
	 * used to compute the rotation matrix that transforms the point from the world space
	 * to the view space.
	 * 
	 * 	- `up` is a `Vector3f` object that represents an upward direction vector in 3D
	 * space. It has x, y, and z components, which are typically non-negative values
	 * representing the magnitude of the vector.
	 * 	- The `up` vector is used to compute the rotation matrix that transforms the
	 * original position of a 3D object to its look-at position.
	 * 
	 * @returns a Quaternion representing the rotation needed to look at a point in 3D
	 * space from a specific position and direction.
	 * 
	 * 	- The output is a `Quaternion` object, which represents a 3D rotation transformation.
	 * 	- The quaternion is generated using the rotational component of the input vector
	 * `point - pos`, which is normalized to ensure that the rotation is properly oriented.
	 * 	- The up vector in the input is used to determine the orientation of the rotation.
	 * 
	 * Overall, the function returns a quaternion that represents the rotation needed to
	 * look at a point in 3D space from a specific viewpoint, based on two input vectors
	 * representing the position and orientation of the viewer.
	 */
	public Quaternion getLookAtRotation(Vector3f point, Vector3f up) {
		return new Quaternion(new Matrix4f().initRotation(point.sub(pos).normalized(), up));
	}

	/**
	 * checks if any of its variables have changed compared to their previous values. It
	 * returns `true` if any variable has changed, and `false` otherwise.
	 * 
	 * @returns a boolean value indicating whether the object has changed.
	 */
	public boolean hasChanged() {
		if (parent != null && parent.hasChanged())
			return true;

		if (!pos.equals(oldPos))
			return true;

		if (!rot.equals(oldRot))
			return true;

		if (!scale.equals(oldScale))
			return true;

		return false;
	}

	/**
	 * computes and returns a transformation matrix based on the position, rotation, and
	 * scale of an object, using the parent matrix and multiplication operations.
	 * 
	 * @returns a transformed matrix that combines translation, rotation, and scaling effects.
	 * 
	 * 	- The multiplication of the parent matrix `getParentMatrix()` with the translation
	 * matrix `translationMatrix`, rotation matrix `rotationMatrix`, and scale matrix
	 * `scaleMatrix` results in the final transformation matrix.
	 * 	- The translation matrix `translationMatrix` represents a displacement in 3D
	 * space, with its elements indicating the x, y, and z coordinates of the displacement.
	 * 	- The rotation matrix `rotationMatrix` represents a rotation around a central
	 * axis, with its elements indicating the amount of rotation in each axis (x, y, and
	 * z).
	 * 	- The scale matrix `scaleMatrix` represents a scaling operation, with its elements
	 * indicating the amount of scaling along each axis (x, y, and z).
	 * 
	 * Therefore, the output of the `getTransformation` function is a transformation
	 * matrix that combines these three operations (translation, rotation, and scaling)
	 * to produce the final transformation.
	 */
	public Matrix4f getTransformation() {
		Matrix4f translationMatrix = new Matrix4f().initTranslation(pos.getX(), pos.getY(), pos.getZ());
		Matrix4f rotationMatrix = rot.toRotationMatrix();
		Matrix4f scaleMatrix = new Matrix4f().initScale(scale.getX(), scale.getY(), scale.getZ());

		return getParentMatrix().mul(translationMatrix.mul(rotationMatrix.mul(scaleMatrix)));
	}

	/**
	 * retrieves and returns the transformation matrix of its parent node, taking into
	 * account any changes to the parent's transformation.
	 * 
	 * @returns a Matrix4f object representing the transformation matrix of the parent node.
	 * 
	 * 	- `parentMatrix`: This is a Matrix4f object representing the parent transformation
	 * matrix of the current transform.
	 * 	- `hasChanged()`: This method checks if the parent transformation matrix has been
	 * modified since the last call to the `getParentMatrix` function. If the matrix has
	 * changed, it returns the updated matrix. Otherwise, it returns the same matrix as
	 * before.
	 */
	private Matrix4f getParentMatrix() {
		if (parent != null && parent.hasChanged())
			parentMatrix = parent.getTransformation();

		return parentMatrix;
	}

	/**
	 * sets the `parent` field to a given `Transform` object, allowing for the inheritance
	 * of properties and methods from the parent object.
	 * 
	 * @param parent Transform object that this instance will be the child of, and it is
	 * assigned to the `parent` field of the current class.
	 * 
	 * 	- Type: `Transform` - represents a transformer object in the parent class hierarchy.
	 * 	- Value: The deserialized value of the `parent` field, which is a reference to
	 * another transformer object.
	 */
	public void setParent(Transform parent) {
		this.parent = parent;
	}

	/**
	 * transforms a `Vector3f` object using the matrix provided by `getParentMatrix`. The
	 * transformed position is then returned.
	 * 
	 * @returns a transformed position vector.
	 * 
	 * 	- The Vector3f object represents the transformed position of an object in 3D space
	 * after applying a matrix transformation.
	 * 	- The transformation is applied to the input position vector using the `transform()`
	 * method of the parent matrix.
	 * 	- The resulting transformed position vector contains the updated coordinates after
	 * application of the matrix transformation.
	 */
	public Vector3f getTransformedPos() {
		return getParentMatrix().transform(pos);
	}

	/**
	 * takes a `Quaternion` object `parentRotation` and multiplies it by another `Quaternion`
	 * object `rot`, returning the transformed rotation.
	 * 
	 * @returns a Quaternion representation of the transformed rotation matrix multiplied
	 * by the input rotation matrix.
	 * 
	 * The `Quaternion` object represents a rotation transformation that is the result
	 * of multiplying the parent rotation (`parentRotation`) by the rotational component
	 * (`rot`). This multiplication results in a new rotation representation that combines
	 * the two input rotations.
	 */
	public Quaternion getTransformedRot() {
		Quaternion parentRotation = new Quaternion(1, 0, 0, 0);

		if (parent != null)
			parentRotation = parent.getTransformedRot();

		return parentRotation.mul(rot);
	}

	/**
	 * returns the position of an object in 3D space as a `Vector3f` object.
	 * 
	 * @returns a reference to a `Vector3f` object representing the position of an entity.
	 * 
	 * 	- The `Vector3f` object represents a 3D point in homogeneous coordinates, consisting
	 * of x, y, and z components.
	 * 	- The `pos` field is a member variable of the class that stores the position of
	 * an entity in a 3D space.
	 * 	- The `getPos` function simply returns the value of the `pos` field, allowing for
	 * easy access to the position of the entity.
	 */
	public Vector3f getPos() {
		return pos;
	}

	/**
	 * sets the position of an object to a new value represented as a Vector3f.
	 * 
	 * @param pos 3D position of an object to which the method is being applied, and it
	 * is assigned the value of the `pos` argument passed to the function.
	 * 
	 * 	- `this.pos`: It is a Vector3f instance field that represents the position of an
	 * object in 3D space.
	 * 	- `pos`: It is an instance field that holds the vector representation of the
	 * position of an object in 3D space.
	 * 	- Other properties/attributes, such as x, y, and z components of the position
	 * vector, are also present in the `pos` instance field.
	 */
	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

	/**
	 * adds a vector to the position of an object, updating its new position based on the
	 * sum of the current position and the input vector.
	 * 
	 * @param addVec 3D vector to be added to the current position of the object.
	 * 
	 * 	- `Vector3f`: This class represents a 3D vector with three components (x, y, and
	 * z).
	 * 	- `setPos()`: A method that sets the position of an object. It takes a `Vector3f`
	 * argument representing the new position.
	 */
	public void addToPos(Vector3f addVec) { this.setPos(this.getPos().add(addVec)); }

	/**
	 * returns a `Quaternion` object representing the rotation component of an entity.
	 * 
	 * @returns a Quaternion object representing the rotation of the game object.
	 * 
	 * 1/ The `rot` variable is a Quaternion object representing the rotation of the entity.
	 * 2/ It has four components - x, y, z, and w - which represent the real and imaginary
	 * parts of the quaternion.
	 * 3/ The Quaternion object can be used to perform rotations in 3D space by multiplying
	 * it with other vectors or matrices.
	 */
	public Quaternion getRot() {
		return rot;
	}

	/**
	 * sets the object's rotational orientation to the specified Quaternion value.
	 * 
	 * @param rotation 4D quaternion value that modifies the rotational orientation of
	 * the object being modeled, as represented by the `this.rot` field.
	 * 
	 * 	- Quaternion: This indicates that `rotation` is a mathematical object representing
	 * a quaternion, which is a number of the form `a + bi + cj + dk`, where `a`, `b`,
	 * `c`, and `d` are real numbers and `i`, `j`, and `k` are imaginary units that satisfy
	 * certain rules.
	 * 	- Public: This indicates that the function is intended to be accessed from outside
	 * the class, which means it can be called by other parts of the program.
	 * 	- Void: This indicates that the function does not return any value after its
	 * execution. Instead, it modifies the internal state of the object on which it is called.
	 */
	public void setRot(Quaternion rotation) {
		this.rot = rotation;
	}

	/**
	 * returns the current scale value of the object.
	 * 
	 * @returns a vector of type `Vector3f`, containing the object's scale.
	 * 
	 * The Vector3f object `scale` represents a 3D vector that contains the scale values
	 * for the specified axis. The values range from -1 to 1 and can be used to modify
	 * or transform objects in 3D space.
	 */
	public Vector3f getScale() {
		return scale;
	}

	/**
	 * sets the scaling factor of an object, by assigning a new `Vector3f` instance to
	 * the `scale` field of the class.
	 * 
	 * @param scale 3D scaling factor for the object, which is assigned to the `scale`
	 * field of the class.
	 * 
	 * 	- The `Vector3f` class represents a 3D vector with real-valued components.
	 * 	- It has three fields: x, y, and z, which represent the coordinates of the vector.
	 * 	- These fields can take on any real value within their respective ranges, allowing
	 * for precise control over the vector's orientation in 3D space.
	 */
	public void setScale(Vector3f scale) {
		this.scale = scale;
	}
	
	/**
	 * returns an empty string for the class `Object`.
	 * 
	 * @returns an empty string.
	 * 
	 * 1/ The output is an empty string (""). This indicates that the object has no
	 * inherent identity or value beyond its class and state.
	 * 2/ The lack of any content in the output suggests that the object does not have
	 * any information or attributes that can be expressed through a string representation.
	 * 3/ The use of an empty string as the return type implies that the `toString`
	 * function is intended to be used for informational purposes only, and should not
	 * be relied upon for actual data retrieval or manipulation.
	 */
	@Override
	public String toString() { return "";
	}

}
