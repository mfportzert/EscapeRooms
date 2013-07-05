package com.mfp.rooms.activities;

import java.io.IOException;
import java.lang.reflect.Constructor;

import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.UncoloredSprite;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.opengl.shader.PositionTextureCoordinatesShaderProgram;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.shader.exception.ShaderProgramException;
import org.andengine.opengl.shader.exception.ShaderProgramLinkException;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.render.RenderTexture;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;

import android.opengl.GLES20;
import android.os.AsyncTask;
import android.util.Log;

import com.mfp.rooms.camera.AdjustedSmoothCamera;
import com.mfp.rooms.camera.CameraManager;
import com.mfp.rooms.gui.LevelGui;
import com.mfp.rooms.interfaces.ILevelActivity;
import com.mfp.rooms.scenes.ErrorScene;
import com.mfp.rooms.scenes.MoreComingScene;
import com.mfp.rooms.scenes.base.BaseLevel;
import com.mfp.rooms.scenes.base.BaseScene;
import com.mfp.rooms.utils.LevelUtils;

/**
 * 
 * @author M-F.P
 */
public class LevelActivity extends SimpleBaseGameActivity implements ILevelActivity {
	
    // ===========================================================
    // Constants
    // ===========================================================
	
    public static final String EXTRA_LEVEL_NUMBER = "level_numer";
    
    public static final int LEVELS_NUMBER = 20;    
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    private BaseScene mCurrentScene;
    
    private LevelGui mGui;
    private AdjustedSmoothCamera mCamera;
    private boolean mAccessingNextLevel = false;
    
    private boolean mRadialBlurring = false;
	private float mRadialBlurCenterX = 0.5f;
	private float mRadialBlurCenterY = 0.5f;
	
	private RadialBlurShaderProgram mRadialBlurShaderProgram;
	
    /*
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    */
    // ===========================================================
    // Constructors
    // ===========================================================
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    /*
    @Override
    protected void onSetContentView() {
        
    	final FrameLayout frameLayout = new FrameLayout(this);
        final FrameLayout.LayoutParams frameLayoutLayoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
                                             FrameLayout.LayoutParams.FILL_PARENT);
 
        final AdView adView = new AdView(this, AdSize.BANNER, "PUBLISHER_ID");
 
        adView.refreshDrawableState();
        adView.setVisibility(AdView.VISIBLE);
        final FrameLayout.LayoutParams adViewLayoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                             FrameLayout.LayoutParams.WRAP_CONTENT,
                                             Gravity.CENTER_HORIZONTAL|Gravity.TOP);
       
        AdRequest adRequest = new AdRequest();
        adView.loadAd(adRequest);
 
        this.mRenderSurfaceView = new RenderSurfaceView(this);
        mRenderSurfaceView.setRenderer(mEngine);
 
        final android.widget.FrameLayout.LayoutParams surfaceViewLayoutParams =
                new FrameLayout.LayoutParams(super.createSurfaceViewLayoutParams());
 
        frameLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);
        frameLayout.addView(adView, adViewLayoutParams);
 
        this.setContentView(frameLayout, frameLayoutLayoutParams);
    }
    */
	
    @Override
    public EngineOptions onCreateEngineOptions() {
    	
    	CameraManager.CAMERA_WIDTH = this.getResources().getDisplayMetrics().widthPixels;
    	CameraManager.CAMERA_HEIGHT = this.getResources().getDisplayMetrics().heightPixels;
    	
        this.mCamera = new AdjustedSmoothCamera(0, 0, CameraManager.CAMERA_WIDTH, CameraManager.CAMERA_HEIGHT, 
        		CameraManager.CAMERA_WIDTH, CameraManager.CAMERA_HEIGHT, 2);
        
        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
                new RatioResolutionPolicy(CameraManager.CAMERA_WIDTH, CameraManager.CAMERA_HEIGHT), 
                this.mCamera);
        //engineOptions.getTouchOptions().setNeedsMultiTouch(true);
        engineOptions.getAudioOptions().setNeedsSound(true);
        
        // TODO: Gérer avec un message les utilisateurs qui ne peuvent pas bénéficier du multi touch
        /*
        if (MultiTouch.isSupported(this)) {
        	
        	if (MultiTouch.isSupportedDistinct(this)) {
        		Toast.makeText(this, "MultiTouch detected --> Both controls will work properly!", Toast.LENGTH_SHORT).show();
        	} else {
        		Toast.makeText(this, "MultiTouch detected, but your device has problems distinguishing between fingers.\n\nControls are placed at different vertical locations.", Toast.LENGTH_LONG).show();
        	}
        } else {
        	Toast.makeText(this, "Sorry your device does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)\n\nControls are placed at different vertical locations.", Toast.LENGTH_LONG).show();
        }
		*/
        
        return engineOptions;
    }
    
    @Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
    	
    	mRadialBlurShaderProgram = new RadialBlurShaderProgram();
    	
		return new Engine(pEngineOptions) {
			private boolean mRenderTextureInitialized;

			private RenderTexture mRenderTexture;
			private UncoloredSprite mRenderTextureSprite;

			@Override
			public void onDrawFrame(final GLState pGLState) throws InterruptedException {
				final boolean firstFrame = !this.mRenderTextureInitialized;

				if(firstFrame) {
					this.initRenderTextures(pGLState);
					this.mRenderTextureInitialized = true;
				}

				final int surfaceWidth = this.mCamera.getSurfaceWidth();
				final int surfaceHeight = this.mCamera.getSurfaceHeight();

				this.mRenderTexture.begin(pGLState);
				{
					/* Draw current frame. */
					super.onDrawFrame(pGLState);
				}
				this.mRenderTexture.end(pGLState);

				/* Draw rendered texture with custom shader. */
				{
					pGLState.pushProjectionGLMatrix();
					pGLState.orthoProjectionGLMatrixf(0, surfaceWidth, 0, surfaceHeight, -1, 1);
					{
						this.mRenderTextureSprite.onDraw(pGLState, this.mCamera);
					}
					pGLState.popProjectionGLMatrix();
				}
			}

			private void initRenderTextures(final GLState pGLState) {
				final int surfaceWidth = this.mCamera.getSurfaceWidth();
				final int surfaceHeight = this.mCamera.getSurfaceHeight();

				this.mRenderTexture = new RenderTexture(getTextureManager(), surfaceWidth, surfaceHeight);
				this.mRenderTexture.init(pGLState);

				final ITextureRegion renderTextureTextureRegion = TextureRegionFactory.extractFromTexture(this.mRenderTexture);
				this.mRenderTextureSprite = new UncoloredSprite(0, 0, renderTextureTextureRegion, this.getVertexBufferObjectManager()) {
					@Override
					protected void preDraw(final GLState pGLState, final Camera pCamera) {
						if(mRadialBlurring) {
							this.setShaderProgram(mRadialBlurShaderProgram);
						} else {
							this.setShaderProgram(PositionTextureCoordinatesShaderProgram.getInstance());
						}
						super.preDraw(pGLState, pCamera);

						GLES20.glUniform2f(RadialBlurShaderProgram.sUniformRadialBlurCenterLocation, 
								mRadialBlurCenterX, 1 - mRadialBlurCenterY);
					}
				};
			}
		};
	}
    
    @Override
    public void onCreateResources() {
    	
    	SoundFactory.setAssetBasePath("mfx/");
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        try {
        	this.mCurrentScene = this.createLevel(this.getIntent().getIntExtra(EXTRA_LEVEL_NUMBER, 1));
        	if (this.mGui == null) {
            	this.mGui = new LevelGui(this.mEngine, this);
    	        this.mCamera.setHUD(this.mGui);
            }
        	
        } catch (Exception e) {
			this.mCurrentScene = new ErrorScene(this.mCamera);
			Debug.e(e);
		}
        
        try {
        	this.mCurrentScene.createResources(this.mEngine, this);
        } catch (IOException e) {
    		Debug.e(e);
    	}
    }
    
    @Override
    public Scene onCreateScene() {

    	//final FPSCounter fpsCounter = new FPSCounter();
    	//this.mEngine.registerUpdateHandler(fpsCounter);
    	//this.mGui.activateFps(fpsCounter);
    	
        if (this.mCurrentScene instanceof BaseLevel) {
        	this.mGui.build();
        }

    	this.mCurrentScene.createScene(this.mEngine, this);
        return this.mCurrentScene;
    }
    
    @Override
    public void accessNextLevel(final float zoomToX, final float zoomToY) {
		
    	if (this.mAccessingNextLevel) {
    		return;
    	}
    	
    	this.mAccessingNextLevel = true;
    	this.mCurrentScene.registerEntityModifier(new DelayModifier(1.5f, new IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				
				mRadialBlurring = true;
				
				mRadialBlurCenterX = zoomToX / mCurrentScene.getWidth();
				mRadialBlurCenterY = zoomToY / mCurrentScene.getHeight();
				
				mCamera.setCenter(zoomToX, zoomToY);
		    	mCamera.setZoomFactor(2f);
		    	mGui.getBlackScreen().registerEntityModifier(new AlphaModifier(0.5f, 0.0f, 1.0f));
			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				mGui.displayLoading(true);
				loadLevel(((BaseLevel) mCurrentScene).getLevelNumber() + 1);
			}
		}));
		
	    this.onResumeGame();
	}
    
    @Override
	public void restart() {
		
    	BaseLevel level = (BaseLevel) this.mCurrentScene;
    	float scaleX = level.getBackgroundSprite().getScaleX();
    	float scaleY = level.getBackgroundSprite().getScaleY();
    	
    	this.mCurrentScene.reset();
    	// TODO: Make sure the inventory is reset as well
        //this.mGui.reset();
        this.mGui.clearChildScene();
        this.mGui.getMenuScene().reset();
        
        level.getBackgroundSprite().setScale(scaleX, scaleY);
	}
    
    @Override
    public void pause() {
        
        if (this.mGui.hasChildScene()) {
            this.mGui.getMenuScene().back();
            this.mCurrentScene.setIgnoreUpdate(false);
        } else {
        	this.mGui.setChildScene(this.mGui.getMenuScene(), false, true, true);
            this.mCurrentScene.setIgnoreUpdate(true);
        }
    }
    
	@Override
	public void onBackPressed() {
		if (this.mGui != null && this.mGui.hasChildScene()) {
            this.pause();
        } else {
        	super.onBackPressed();
        }
	}
	
    // ===========================================================
    // Methods
    // ===========================================================
    
    private BaseLevel createLevel(int levelNumber) throws Exception {
    	
		Constructor<?> constructeur = Class.forName(
				BaseLevel.LEVEL_PKG_NAME+LevelUtils.toLevelNbFormat(levelNumber))
					.getConstructor(SmoothCamera.class);
		
		BaseLevel newLevel = (BaseLevel) constructeur.newInstance(this.mCamera);
		newLevel.setLevelActivity(this);
		
		if (newLevel instanceof IAccelerationListener) {
			this.mEngine.enableAccelerationSensor(this, (IAccelerationListener) newLevel);
		} else {
			this.mEngine.disableAccelerationSensor(this);
		}
		
		return newLevel;
    }
    
    private void loadLevel(int levelNumber) {
    	
    	mRadialBlurring = false;
    	
    	this.mCurrentScene.clearTouchAreas();
		this.mCurrentScene.detachChildren();
		this.mEngine.clearUpdateHandlers();
	    
		BaseScene newScene = null;
		if (levelNumber < LevelActivity.LEVELS_NUMBER) {
		    try {
		    	newScene = this.createLevel(levelNumber);
		    } catch (Exception e) {
		    	newScene = new ErrorScene(this.mCamera);
				Debug.e(e);
			}
		    
		} else {
			newScene = new MoreComingScene(this.mCamera);
		}
		
		this.mCamera.setHUD((newScene instanceof BaseLevel) ? this.mGui : null);
		
		try {
			newScene.createResources(this.mEngine, this);
		} catch (IOException e) {
			Debug.e(e);
		}
		
		newScene.createScene(this.mEngine, this);
		
		this.mEngine.setScene(newScene);
		this.mCurrentScene = newScene;
		
		this.mGui.displayLoading(false);
		this.mGui.getBlackScreen().setAlpha(0);
		
		this.mCamera.setCenterDirect(mCurrentScene.getCenterX(), mCurrentScene.getCenterY());
		this.mCamera.setZoomFactorDirect(1f);
		this.mAccessingNextLevel = false;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    
    public static class RadialBlurShaderProgram extends ShaderProgram {
		// ===========================================================
		// Constants
		// ===========================================================

		//private static RadialBlurShaderProgram INSTANCE;

		public static final String VERTEXSHADER =
			"uniform mat4 " + ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + ";\n" +
			"attribute vec4 " + ShaderProgramConstants.ATTRIBUTE_POSITION + ";\n" +
			"attribute vec2 " + ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES + ";\n" +
			"varying vec2 " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n" +
			"void main() {\n" +
			"	" + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " = " + ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES + ";\n" +
			"	gl_Position = " + ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + " * " + ShaderProgramConstants.ATTRIBUTE_POSITION + ";\n" +
			"}";

		private static final String UNIFORM_RADIALBLUR_CENTER = "u_radialblur_center";

		public static final String FRAGMENTSHADER =
			"precision lowp float;\n" +

			"uniform sampler2D " + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ";\n" +
			"varying mediump vec2 " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n" +

			"uniform vec2 " + RadialBlurShaderProgram.UNIFORM_RADIALBLUR_CENTER + ";\n" +

			"const float sampleShare = (1.0 / 11.0);\n" +
			"const float sampleDist = 0.75;\n" +
			"const float sampleStrength = 1.0;\n" +

			"void main() {\n" +
			/* The actual (unburred) sample. */
			"	vec4 color = texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ");\n" +

			/* Calculate direction towards center of the blur. */
			"	vec2 direction = " + RadialBlurShaderProgram.UNIFORM_RADIALBLUR_CENTER + " - " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n" +

			/* Calculate the distance to the center of the blur. */
			"	float distance = sqrt(direction.x * direction.x + direction.y * direction.y);\n" +

			/* Normalize the direction (reuse the distance). */
			"	direction = direction / distance;\n" +

			"	vec4 sum = color * sampleShare;\n" +
			/* Take 10 additional samples along the direction towards the center of the blur. */
			"	vec2 directionSampleDist = direction * sampleDist;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " - 0.08 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " - 0.05 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " - 0.03 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " - 0.02 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " - 0.01 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " + 0.01 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " + 0.02 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " + 0.03 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " + 0.05 * directionSampleDist) * sampleShare;\n" +
			"	sum += texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " + 0.08 * directionSampleDist) * sampleShare;\n" +

			/* Weighten the blur effect with the distance to the center of the blur (further out is blurred more). */
			"	float t = sqrt(distance) * sampleStrength;\n" +
			"	t = clamp(t, 0.0, 1.0);\n" + // 0 <= t >= 1

			/* Blend the original color with the averaged pixels. */
			"	gl_FragColor = mix(color, sum, t);\n" +
			"}";

		// ===========================================================
		// Fields
		// ===========================================================

		public static int sUniformModelViewPositionMatrixLocation = ShaderProgramConstants.LOCATION_INVALID;
		public static int sUniformTexture0Location = ShaderProgramConstants.LOCATION_INVALID;
		public static int sUniformRadialBlurCenterLocation = ShaderProgramConstants.LOCATION_INVALID;

		// ===========================================================
		// Constructors
		// ===========================================================

		private RadialBlurShaderProgram() {
			super(RadialBlurShaderProgram.VERTEXSHADER, RadialBlurShaderProgram.FRAGMENTSHADER);
		}
/*
		public static RadialBlurShaderProgram getInstance() {
			
			// FIXME: Here is a bug occuring where going back and recreating LevelActivity but using 
			// the same static instance on RadialBlurShaderProgram().. Screen is just black..
			//if(RadialBlurShaderProgram.INSTANCE == null) {
				RadialBlurShaderProgram.INSTANCE = new RadialBlurShaderProgram();
			//}
			return RadialBlurShaderProgram.INSTANCE;
		}*/

		// ===========================================================
		// Getter & Setter
		// ===========================================================

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		@Override
		protected void link(final GLState pGLState) throws ShaderProgramLinkException {
			GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_POSITION_LOCATION, ShaderProgramConstants.ATTRIBUTE_POSITION);
			GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES_LOCATION, ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES);

			super.link(pGLState);

			RadialBlurShaderProgram.sUniformModelViewPositionMatrixLocation = this.getUniformLocation(ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX);
			RadialBlurShaderProgram.sUniformTexture0Location = this.getUniformLocation(ShaderProgramConstants.UNIFORM_TEXTURE_0);

			RadialBlurShaderProgram.sUniformRadialBlurCenterLocation = this.getUniformLocation(RadialBlurShaderProgram.UNIFORM_RADIALBLUR_CENTER);
		}

		@Override
		public void bind(final GLState pGLState, final VertexBufferObjectAttributes pVertexBufferObjectAttributes) {
			GLES20.glDisableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);

			super.bind(pGLState, pVertexBufferObjectAttributes);

			GLES20.glUniformMatrix4fv(RadialBlurShaderProgram.sUniformModelViewPositionMatrixLocation, 1, false, pGLState.getModelViewProjectionGLMatrix(), 0);
			GLES20.glUniform1i(RadialBlurShaderProgram.sUniformTexture0Location, 0);
		}

		@Override
		public void unbind(final GLState pGLState) throws ShaderProgramException {
			GLES20.glEnableVertexAttribArray(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION);

			super.unbind(pGLState);
		}

		// ===========================================================
		// Methods
		// ===========================================================

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
}