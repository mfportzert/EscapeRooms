/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfp.rooms.gui;

import java.util.ArrayList;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSCounter;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.color.Color;

import android.content.Context;
import android.graphics.Typeface;
import android.opengl.GLES20;

import com.mfp.rooms.camera.CameraManager;
import com.mfp.rooms.interfaces.ILevelActivity;
import com.mfp.rooms.inventory.Inventory;
import com.mfp.rooms.inventory.Item;
import com.mfp.rooms.sprites.SelectableButtonSprite;
import com.mfp.rooms.sprites.SelectableButtonSprite.OnClickListener;
import com.mfp.rooms.sprites.SelectableButtonSprite.State;
import com.mfp.rooms.texturepacker.TxGui;
import com.mfp.rooms.textures.TexturesFactory;
import com.mfp.rooms.utils.LevelUtils;

/**
 *
 * @author M-F.P
 */
public class LevelGui extends HUD implements IOnSceneTouchListener, IOnMenuItemClickListener, TxGui {
    
	// ===========================================================
    // Constants
    // ===========================================================
	
	private final static float GUI_SIZE = 1.2f;
    private final static int FONT_SIZE = 25;
    
    private static final int NB_ITEMS = 6;
    
    private static final int[] MENU_ITEMS = new int[] { 
    	MENU_RESTART_BUTTON_ID,
    	MENU_HOME_BUTTON_ID,
    	MENU_SETTINGS_BUTTON_ID,
    	MENU_RESUME_BUTTON_ID
    };
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    private ArrayList<SelectableButtonSprite> mInventoryItemBoxes = new ArrayList<SelectableButtonSprite>(NB_ITEMS);
    private SelectableButtonSprite mInventoryBagButton;
    private Sprite mAdView;
    private Sprite mLoadingLabel;
    private Sprite mInventoryItemsWindow;
    private Sprite mPauseMenuWindow;
    private AnimatedSprite mLoadingAnimation;
    private ButtonSprite mPauseButton;
    private Rectangle mBlackScreen;
    
    private Engine mEngine;
    private TexturesFactory mGuiTexturesFactory;
    
    private Font mDefaultFont;
    private Font mFffTusjFont;
    private Font mSketchetikFont;
    private Text mFpsText;
    
    private BitmapTextureAtlas mFontTexture;
    private BitmapTextureAtlas mFffTusjFontTexture;
    private BitmapTextureAtlas mSketchetikFontTexture;
    
    private MenuScene mMenuScene;
    private ILevelActivity mLevelActivity;
    private Inventory mInventory = new Inventory();
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    /**
     * 
     * @param pLevel
     * @param pEngine
     * @param pContext
     */
    public LevelGui(final Engine pEngine, final Context pContext) {
        
    	this.mEngine = pEngine;
    	this.mLevelActivity = (ILevelActivity) pContext;
    	
        this.loadFonts(pEngine, pContext);
        this.mGuiTexturesFactory = new TexturesFactory();
		this.mGuiTexturesFactory.loadSpritesheets(pEngine, pContext, "xml/gui.xml");
        
		this.mBlackScreen = new Rectangle(0, 0, CameraManager.DEFAULT_CAMERA_WIDTH, 
				CameraManager.DEFAULT_CAMERA_HEIGHT, pEngine.getVertexBufferObjectManager()) {
			
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, 
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				
				return this.isVisible();
			}
		};
		this.mBlackScreen.setColor(Color.BLACK);
		this.mBlackScreen.setAlpha(0.0f);
		
		LevelUtils.scaleToScreen(this, CameraManager.DEFAULT_CAMERA_WIDTH, 
				CameraManager.DEFAULT_CAMERA_HEIGHT);
		
		/*
        this.mFpsText = new Text(0, 30, this.mDefaultFont, "FPS:", 
        	"FPS: XXXXXXXXXXX".length(), pEngine.getVertexBufferObjectManager());
        */
		
        this.setOnSceneTouchListener(this);
        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setOnAreaTouchTraversalFrontToBack();
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    public MenuScene getMenuScene() {
    	return this.mMenuScene;
    }
    
 	// ===========================================================
 	// Methods for/from SuperClass/Interfaces
 	// ===========================================================
    
    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
        
		if (this.mInventoryBagButton.getState() == State.SELECTED &&
				!this.mInventoryItemsWindow.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
			this.closeInventoryBag();
		}
    	return false;
    }
    
    @Override
    public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, 
	    final float pMenuItemLocalX, final float pMenuItemLocalY) {
	
        switch (pMenuItem.getID()) {
        	
           	case MENU_RESTART_BUTTON_ID:
                this.mLevelActivity.restart();
                break;
            case MENU_HOME_BUTTON_ID:
                // TODO: Faire une méthode qui détruit l'activité courante pour revenir à la home
            	break;
            case MENU_SETTINGS_BUTTON_ID:
                // TODO: setChildSceneModal(settingsScene);
            	break;
            case MENU_RESUME_BUTTON_ID:
                this.mLevelActivity.pause();
                break;
            default:
                return false;
        }
        
        return true;
    }
    
    // ===========================================================
 	// Methods
 	// ===========================================================
    
    public Inventory getInventory() {
    	return this.mInventory;
    }
    
    public AnalogOnScreenControl addAnalogController(float x, float y, IAnalogOnScreenControlListener controlListener) {
    	
    	TextureRegion baseControllerRegion = this.mGuiTexturesFactory.getRegion(LevelGui.ONSCREEN_CONTROL_BASE_ID);
    	TextureRegion knobControllerRegion = this.mGuiTexturesFactory.getRegion(LevelGui.ONSCREEN_CONTROL_KNOB_ID);
    	
		final AnalogOnScreenControl onScreenControl = new AnalogOnScreenControl(x, y, 
				this.mCamera, baseControllerRegion,  knobControllerRegion, 0.1f, 
				this.mEngine.getVertexBufferObjectManager(), controlListener);
		onScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		onScreenControl.getControlBase().setAlpha(0.5f);
		
		this.setChildScene(onScreenControl);
		return onScreenControl;
    }
    
    private void loadFonts(final Engine pEngine, final Context pContext) {
        
        this.mFontTexture = new BitmapTextureAtlas(pEngine.getTextureManager(), 
        		256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mFffTusjFontTexture = new BitmapTextureAtlas(pEngine.getTextureManager(), 
        		256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mSketchetikFontTexture = new BitmapTextureAtlas(pEngine.getTextureManager(), 
        		256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        
        FontFactory.setAssetBasePath("font/");
        this.mDefaultFont = new Font(pEngine.getFontManager(), this.mFontTexture, 
        	Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), FONT_SIZE, true, Color.WHITE);
        
        this.mFffTusjFont = FontFactory.createFromAsset(pEngine.getFontManager(), this.mFffTusjFontTexture, 
        		pContext.getAssets(), "FFF_Tusj.ttf", FONT_SIZE, true, Color.WHITE_ARGB_PACKED_INT);
        
        this.mSketchetikFont = FontFactory.createFromAsset(pEngine.getFontManager(), this.mSketchetikFontTexture, 
        		pContext.getAssets(), "Sketchetik-Light.ttf", FONT_SIZE, true, Color.WHITE_ARGB_PACKED_INT);
        
        this.mFontTexture.load();
        this.mSketchetikFontTexture.load();
        this.mFffTusjFontTexture.load();
        
        this.mDefaultFont.load();
        this.mSketchetikFont.load();
        this.mFffTusjFont.load();
    }
    
    private void buildMenuScene() {
    	
    	this.mMenuScene = new MenuScene(this.mCamera);
    	
    	TextureRegion menuWindowTopRegion = this.mGuiTexturesFactory.getRegion(MENU_WINDOW_TOP_ID);
    	TextureRegion menuWindowLineRegion = this.mGuiTexturesFactory.getRegion(MENU_WINDOW_LINE_ID);
    	TextureRegion menuWindowBottomRegion = this.mGuiTexturesFactory.getRegion(MENU_WINDOW_BOTTOM_ID);
    	
    	this.mPauseMenuWindow = new Sprite(
    			(CameraManager.DEFAULT_CAMERA_WIDTH - menuWindowTopRegion.getWidth()) / 2, 
    			(CameraManager.DEFAULT_CAMERA_HEIGHT - (menuWindowTopRegion.getHeight() + 
    					(menuWindowLineRegion.getHeight() * MENU_ITEMS.length) + 
    					menuWindowBottomRegion.getHeight())) / 2, 
    			menuWindowTopRegion, this.mEngine.getVertexBufferObjectManager());
    	
    	Rectangle blackBackground = new Rectangle(0, 0, CameraManager.DEFAULT_CAMERA_WIDTH, 
    			CameraManager.DEFAULT_CAMERA_HEIGHT, this.mEngine.getVertexBufferObjectManager());
    	blackBackground.setColor(Color.BLACK);
    	blackBackground.setAlpha(0.5f);
    	
    	this.mMenuScene.attachChild(blackBackground);
    	//this.mMenuScene.attachChild(this.mPauseMenuWindow);
    	
    	float offsetY = this.mPauseMenuWindow.getHeight();
    	for (int i = 0; i < MENU_ITEMS.length; i++) {
    		
    		this.mPauseMenuWindow.attachChild(new Sprite(0, offsetY, menuWindowLineRegion, 
    				this.mEngine.getVertexBufferObjectManager()));
    		
    		final SpriteMenuItem menuItem = new SpriteMenuItem(MENU_ITEMS[i], 
    				this.mGuiTexturesFactory.getRegion(MENU_ITEMS[i]), 
                    this.mEngine.getVertexBufferObjectManager());
    		menuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    		
            this.mMenuScene.addMenuItem(menuItem);
    		offsetY += menuWindowLineRegion.getHeight();
    	}
    	
        this.mMenuScene.buildAnimations();
        
        this.mMenuScene.setBackgroundEnabled(false);
        this.mMenuScene.setOnMenuItemClickListener(this);
    	
    	this.mPauseMenuWindow.attachChild(new Sprite(0, offsetY, menuWindowBottomRegion, 
    			this.mEngine.getVertexBufferObjectManager()));
    }
    
    private void buildInventary() {
    	
    	TextureRegion inventaryItemsWindowsRegion = this.mGuiTexturesFactory.getRegion(ITEMS_WINDOW_ID);
    	TiledTextureRegion inventaryItemBoxTiledRegion = this.mGuiTexturesFactory.getTiled(ITEM_BOX_ID, 1, 2);
    	
    	this.mInventoryItemsWindow = new ButtonSprite(
    			CameraManager.DEFAULT_CAMERA_WIDTH - ((inventaryItemsWindowsRegion.getWidth() + 40) * GUI_SIZE), 
    			this.mInventoryBagButton.getY() - 130,
    			inventaryItemsWindowsRegion, this.mEngine.getVertexBufferObjectManager());
    	this.mInventoryItemsWindow.setAlpha(0.9f);
    	
    	float x = 3;
    	float y = 30;
    	for (int i = 0; i < 2; i++) {
    		
    		for (int j = 0; j < 3; j++) {
    			
    			SelectableButtonSprite itemBox = new SelectableButtonSprite(x, y, 
    					inventaryItemBoxTiledRegion, this.mEngine.getVertexBufferObjectManager(),
    					this.mInventoryItemBoxButtonOnClickListener);
    			itemBox.setScale(0.90f);
    			
    			this.mInventoryItemBoxes.add(itemBox);
    			this.mInventoryItemsWindow.attachChild(itemBox);
    			this.registerTouchArea(itemBox);
    			x += inventaryItemBoxTiledRegion.getWidth(0) - 8;
    		}
    		
    		y += inventaryItemBoxTiledRegion.getHeight(0) - 10;
    		x = 3;
    	}
    	
    	this.mInventoryBagButton.setAlpha(0.95f);
    	this.mInventoryBagButton.setScale(GUI_SIZE);
    	this.mInventoryItemsWindow.setScale(GUI_SIZE - 0.05f);
    	this.closeInventoryBag();
    	
    	this.attachChild(this.mInventoryItemsWindow);
    }
    
    public Rectangle getBlackScreen() {
    	return this.mBlackScreen;
    }
    
    private void buildLoading() {
    	
    	TextureRegion loadingTextWindowsRegion = this.mGuiTexturesFactory.getRegion(LOADING_TXT_ID);
    	TiledTextureRegion loadingAnimationTiledRegion = this.mGuiTexturesFactory.getTiled(LOADING_ID, 1, 9);
    	
    	this.mLoadingLabel = new Sprite(
    			(CameraManager.DEFAULT_CAMERA_WIDTH / 2) - (loadingTextWindowsRegion.getWidth() / 2), 
    			this.mAdView.getY() - loadingTextWindowsRegion.getHeight() - 50,
    			loadingTextWindowsRegion, this.mEngine.getVertexBufferObjectManager());
    	
    	this.mLoadingAnimation = new AnimatedSprite(
    			(CameraManager.DEFAULT_CAMERA_WIDTH / 2) - (loadingAnimationTiledRegion.getWidth(0) / 2) + 50, 
    			this.mAdView.getY() - loadingAnimationTiledRegion.getHeight(0) - 100,
    			loadingAnimationTiledRegion, this.mEngine.getVertexBufferObjectManager());
    	
    	//this.mLoadingAnimation.setAlpha(0);
    	this.mLoadingAnimation.setVisible(false);
    	this.mLoadingAnimation.setScale(2f);
    	//this.mLoadingLabel.setAlpha(0);
    	this.mLoadingLabel.setVisible(false);
    	this.mLoadingLabel.setScale(3f);
    	
    	this.mBlackScreen.attachChild(this.mLoadingLabel);
    	this.mBlackScreen.attachChild(this.mLoadingAnimation);
    }
    
    private void buildHUDButtons() {
    	
    	TiledTextureRegion inventaryBagTiledRegion = this.mGuiTexturesFactory.getTiled(INVENTORY_BAG_ID, 1, 2);
    	TextureRegion pauseButtonRegion = this.mGuiTexturesFactory.getRegion(PAUSE_DARK_ID);
    	
    	this.mInventoryBagButton = new SelectableButtonSprite(
    			CameraManager.DEFAULT_CAMERA_WIDTH - (inventaryBagTiledRegion.getWidth(0) * GUI_SIZE) + 25, 
    			this.mAdView.getY() - (inventaryBagTiledRegion.getHeight(0) * GUI_SIZE) + 30,
    			inventaryBagTiledRegion, this.mEngine.getVertexBufferObjectManager(),
    			this.mInventoryBagButtonOnClickListener);
    	
    	this.mPauseButton = new ButtonSprite(0, 830, pauseButtonRegion, 
    			this.mEngine.getVertexBufferObjectManager(), this.mPauseMenuOnClickListener);
    	this.mPauseButton.setAlpha(0.95f);
    	
    	this.registerTouchArea(this.mPauseButton);
    	this.registerTouchArea(this.mInventoryBagButton);
    	
    	this.attachChild(this.mInventoryBagButton);
    	this.attachChild(this.mPauseButton);
    }
    
    public void build() {
    	
    	TextureRegion adItemRegion = this.mGuiTexturesFactory.getRegion(AD_ID);
    	
    	this.mAdView = new Sprite((CameraManager.DEFAULT_CAMERA_WIDTH - adItemRegion.getWidth()) / 2, 
    			CameraManager.DEFAULT_CAMERA_HEIGHT - adItemRegion.getHeight(), adItemRegion, 
    			this.mEngine.getVertexBufferObjectManager());
    	
    	this.buildHUDButtons();
    	this.buildInventary();
    	this.buildLoading();
    	this.buildMenuScene();
    	
    	this.attachChild(this.mAdView);
    	this.attachChild(this.mBlackScreen);
    }
    
    public void openInventoryBag() {
    	
    	this.mInventoryBagButton.setSelected(true);
    	for (SelectableButtonSprite itemBox : this.mInventoryItemBoxes) {
    		itemBox.setEnabled(true);
    		itemBox.setVisible(true);
    	}
    	this.mInventoryItemsWindow.setVisible(true);
    }
    
    public void closeInventoryBag() {
    	
    	this.mInventoryBagButton.setSelected(false);
    	this.mInventoryItemsWindow.setVisible(false);
    	for (SelectableButtonSprite itemBox : this.mInventoryItemBoxes) {
    		itemBox.setEnabled(false);
    		itemBox.setVisible(false);
    	}
    }
    
    private void updateInventoryItemsGui() {
    	
    	for (SelectableButtonSprite itemBox : this.mInventoryItemBoxes) {
    		if (itemBox.getChildCount() > 0) {
    			itemBox.detachChild(itemBox.getFirstChild());
    		}
    	}
    	
    	int i = 0;
    	for (Item item : this.mInventory.getItems()) {
    		
    		SelectableButtonSprite itemBox = this.mInventoryItemBoxes.get(i);
    		itemBox.attachChild(new Sprite(9, 7, 
    				this.mGuiTexturesFactory.getRegion(item.getType().getRegionId()), 
    				this.mEngine.getVertexBufferObjectManager()));
    		i++;
    	}
	}
    
    public void addInBag(final Item item) {
    	
    	item.detachSelf();
    	this.attachChild(item);
    	
    	float toPosX = this.mInventoryBagButton.getX() + 17 - 
    			((item.getWidth() - 55 > 0) ? ((item.getWidth() - 55) / 2) : 0);
    	Path path = new Path(3).to(item.getX(), item.getY())
    			.to(toPosX, this.mInventoryBagButton.getY() - item.getHeight())
    			.to(toPosX, this.mInventoryBagButton.getY());
		
    	item.registerEntityModifier(new PathModifier(0.5f, path, null, new IPathModifierListener() {
					
				@Override
				public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {
					item.setEnabled(false);
				}
				
				@Override
				public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
					
					if (pWaypointIndex == 1) {
						openInventoryBag();
					}
				}
				
				@Override
				public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
					
				}
				
				@Override
				public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {
					
					item.setVisible(false);
					mInventory.add(item);
					updateInventoryItemsGui();
				}
		}));
    }
    
    public void removeFromBag(Item item) {
    	
    	int selectedIndex = this.mInventory.getSelectedItemIndex();
    	this.mInventoryItemBoxes.get(selectedIndex).setSelected(false);
    	this.mInventory.remove(item);
    	updateInventoryItemsGui();
    }
    
    public void resetItemsSelection() {
    	
    	for (SelectableButtonSprite itemBox : this.mInventoryItemBoxes) {
    		itemBox.setSelected(false);
    	}
    }
    
    public void displayLoading(boolean display) {
    	
    	float duration = 0.2f;
    	float fromAlpha = (display) ? 0f : 1f;
    	float toAlpha = (display) ? 1f : 0f;
    	
    	if (display) {
    		this.mLoadingAnimation.animate(50);
    	} else {
    		this.mLoadingAnimation.stopAnimation();
    	}
    	/*
    	this.mLoadingAnimation.registerEntityModifier(new AlphaModifier(duration, fromAlpha, toAlpha));
    	this.mLoadingLabel.registerEntityModifier(new AlphaModifier(duration, fromAlpha, toAlpha));
    	*/
    	
    	this.mLoadingAnimation.setVisible(display);
    	this.mLoadingLabel.setVisible(display);
    }
    
    /**
     * @param pFPSCounter
     */
    public void activateFps(final FPSCounter pFPSCounter) {
        
        this.attachChild(this.mFpsText);
        this.registerUpdateHandler(new TimerHandler(1 / 20.0f, true, new ITimerCallback() {
        	
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
            	mFpsText.setText("FPS: " + pFPSCounter.getFPS());
            }
        }));
    }
    
    // ===========================================================
 	// Inner and Anonymous Classes
 	// ===========================================================
    
    // ===========================================================
 	// Listeners
 	// ===========================================================
    
	OnClickListener mInventoryBagButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(SelectableButtonSprite pSelectableButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			
			if (mInventoryBagButton.getState() == State.SELECTED) {
				openInventoryBag();
			} else if (mInventoryBagButton.getState() == State.NORMAL) {
				closeInventoryBag();
			}
		}
	};
	
    OnClickListener mInventoryItemBoxButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(SelectableButtonSprite pSelectableButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			
			int selectedItemIndex = mInventory.getSelectedItemIndex();
			int currentItemIndex = mInventoryItemBoxes.indexOf(pSelectableButtonSprite);
			
			if (selectedItemIndex >= 0 && selectedItemIndex != currentItemIndex) {
				mInventoryItemBoxes.get(selectedItemIndex).setSelected(false);
			}
			
			mInventory.select(mInventoryItemBoxes.indexOf(pSelectableButtonSprite), 
					(pSelectableButtonSprite.isSelected()) ? true : false);
		}
	};
	
	ButtonSprite.OnClickListener mPauseMenuOnClickListener = new ButtonSprite.OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			mLevelActivity.pause();
		}
	};
}
