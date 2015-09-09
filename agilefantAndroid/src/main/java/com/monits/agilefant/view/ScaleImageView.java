package com.monits.agilefant.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = "MISSING_TO_STRING_OVERRIDE",
		justification = "There's no need to override toString method on a View class")
public class ScaleImageView extends ImageView {

	/**
	 * We determinate zoom in and zoom out limits. (On scalar measure)
	 */
	private static final float LIMIT_IN = 4f;
	private static final float LIMIT_OUT = 1f;

	// This are all values on a 3x3 Matrix
	private static final int MATRIX_SIZE = 9;

	// Mode enum
	private enum Action {
		DRAG, ZOOM, NONE
	}

	// Action mode
	private Action mode;

	// Determinates if we re zoomed in or not
	private boolean isZoomed;

	// Initial Matrix
	private final Matrix initialMatrix = new Matrix();

	// Mid point between fingers
	@SuppressFBWarnings(value = "FCBL_FIELD_COULD_BE_LOCAL", justification = "False Positive")
	private PointF midPoint;

	// Start point for one finger (Drag action)
	@SuppressFBWarnings(value = "FCBL_FIELD_COULD_BE_LOCAL", justification = "False Positive")
	private final PointF startPoint = new PointF();

	// Distance values are used to store last 2 movements
	@SuppressFBWarnings(value = "FCBL_FIELD_COULD_BE_LOCAL", justification = "False Positive")
	private float oldDistance;

	@SuppressFBWarnings(value = "FCBL_FIELD_COULD_BE_LOCAL", justification = "False Positive")
	private final PointF oldPosition = new PointF();

	/**
	 * @param context context
	 */
	public ScaleImageView(final Context context) {
		super(context);
	}

	/**
	 * @param context context
	 * @param attrs view attrs
	 */
	public ScaleImageView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context context
	 * @param attrs view attrs
	 * @param defStyleAttr def attrs
	 */
	public ScaleImageView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		super.onDraw(canvas);
		// We fix image only if we re not zoomed in
		if (!isZoomed) {
			fixImage();
		}
	}


	@Override
	public boolean onTouchEvent(final MotionEvent motionEvent) {
		switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			// We only want to drag image if we're zoomed in
			if (isZoomed) {
				getParent().requestDisallowInterceptTouchEvent(true);

				// We re executing a dragging action
				mode = Action.DRAG;

				// Initial matrix for dragging
				initialMatrix.set(getImageMatrix());

				// Sets startPoint
				startPoint.set(motionEvent.getX(), motionEvent.getY());

				// Restarts oldPosition
				oldPosition.set(startPoint);
			}

			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			getParent().requestDisallowInterceptTouchEvent(true);

			// We re starting a zoom action (Both fingers re on screen)
			mode = Action.ZOOM;

			// Initial matrix for zooming
			initialMatrix.set(getImageMatrix());

			// Gets mid point between fingers
			midPoint = getMid(motionEvent);

			// Gets initial distance between 2 fingers
			oldDistance = spaceBetweenFingers(motionEvent);

			break;

		case MotionEvent.ACTION_MOVE:
			// if action is zoom, we start zooming
			if (mode == Action.ZOOM) {
				zoom(motionEvent);
			}

			// if action is drag, we start dragging
			if (mode == Action.DRAG) {
				drag(motionEvent);
			}
			break;

		case MotionEvent.ACTION_UP:
			performClick();
			// fallthrough
		case MotionEvent.ACTION_POINTER_UP:
		default:
			//We 've canceled every action
			mode = Action.NONE;
			break;
		}
		return true;
	}

	private void fixImage() {
		// Get bitmap from view
		final Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		final Matrix matrix = new Matrix();
		matrix.set(getImageMatrix());

		// Get image and view dimensions and fit it to matrix
		matrix.set(getImageMatrix());
		final RectF drawableRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF viewRect = new RectF(0, 0, getWidth(), getHeight());
		matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);

		// Update our initial matrix
		initialMatrix.set(matrix);

		// Set matrix to view
		setImageMatrix(matrix);
	}

	private void zoom(final MotionEvent motionEvent) {

		// new distance between fingers
		final float newDistance = spaceBetweenFingers(motionEvent);

		/**
		 * We can't modificate image matrix so we need to create a new instance
		 * and set it's value to image's matrix.
		 */
		final Matrix matrix = new Matrix();
		matrix.set(getImageMatrix());

		// All values on a 3x3 matrix
		final float[] matrixValues = new float[MATRIX_SIZE];

		// fill matrixValues
		matrix.getValues(matrixValues);

		// We don't let user to zoom out more than original position
		if (newDistance < oldDistance && matrixValues[Matrix.MSCALE_X] <= LIMIT_OUT) {

			// Fixes image
			if (isZoomed) {
				fixImage();
			}

			// Isn't zoomed anymore
			isZoomed = false;
			return;
		}

		// We dont let them zoom in more than LIMIT_IN
		if (newDistance > oldDistance && matrixValues[Matrix.MSCALE_X] >= LIMIT_IN) {
			mode = Action.NONE;
			return;
		}

		// get values from matrix and save it on matrixValues
		matrix.getValues(matrixValues);

		// We get scale
		final float scale = newDistance / oldDistance;

		// Copies initial matrix to it
		matrix.set(initialMatrix);

		// We apply zoom
		matrix.postScale(scale, scale, midPoint.x, midPoint.y);
		setImageMatrix(matrix);

		// Is zoomed
		isZoomed = true;

	}

	@SuppressFBWarnings(value = "CLI_CONSTANT_LIST_INDEX", justification = "Are constants used from a specific class.")
	private void drag(final MotionEvent motionEvent) {

		/**
		 * We can't modificate imageMatrix so we need to create
		 * a new instance of matrix and set it's value to imageMatrix.
		 */
		final Matrix matrix = new Matrix();
		matrix.set(getImageMatrix());

		// all values on a 3x3 matrix
		final float[] matrixValues = new float[MATRIX_SIZE];

		// fills matrixValues
		matrix.getValues(matrixValues);

		// We establish borders distance values
		final float left = matrixValues[Matrix.MTRANS_X];
		final float top = matrixValues[Matrix.MTRANS_Y];
		final float right = left + matrixValues[Matrix.MSCALE_X] * getBitmap().getWidth() - getWidth();
		final float bottom = top + matrixValues[Matrix.MSCALE_X] * getBitmap().getHeight() - getHeight();

		float x = motionEvent.getX();
		float y = motionEvent.getY();

		// Limits X edge dragging
		if (motionEvent.getX() > oldPosition.x) {
			//Moving left
			if (left >= 0) {
				x = oldPosition.x;
			}
		} else {
			// Moving right
			if (right <= 0) {
				x = oldPosition.x;
			}
		}

		// Limits Y edge dragging
		if (motionEvent.getY() > oldPosition.y) {
			// Moving top
			if (top >= 0) {
				y = oldPosition.y;
			}
		} else {
			// Moving bottom
			if (bottom <= 0) {
				y = oldPosition.y;
			}
		}

		// Copies initial matrix to it
		matrix.set(initialMatrix);

		matrix.postTranslate(x - startPoint.x, y - startPoint.y);
		setImageMatrix(matrix);

		// Updates old position
		oldPosition.set(x, y);
	}

	private Bitmap getBitmap() {
		return ((BitmapDrawable) getDrawable()).getBitmap();
	}

	private float spaceBetweenFingers(final MotionEvent event) {
		final float x = event.getX() - event.getX(1);
		final float y = event.getY() - event.getY(1);

		return (float) Math.sqrt(x * x + y * y);
	}

	private PointF getMid(final MotionEvent event) {
		final float x = event.getX(0) + event.getX(1);
		final float y = event.getY(0) + event.getY(1);

		return new PointF(x / 2, y / 2);
	}
}
