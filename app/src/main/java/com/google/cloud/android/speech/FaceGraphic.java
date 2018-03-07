/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.android.speech;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.google.cloud.android.speech.camera.GraphicOverlay;

import java.util.HashSet;
import java.util.Set;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
        Color.BLUE
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private volatile Face prevFace;
    private int mFaceId;
    private float mFaceHappiness;
    Landmark bottomMLandmark = null;

    //test stuff
    private String mSpeechText = "";

    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face, boolean spoken, String speechText) {
        if(mFace != null) {
            Log.d("LandMarks: ", "if" );
            prevFace = mFace;
        }
        else {
            Log.d("LandMarks: ", "else" );
            prevFace = face;
        }
        mFace = face;
        mSpeechText = speechText;
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        Face pFace = prevFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);

        float px = translateX(pFace.getPosition().x + face.getWidth() / 2);
        float py = translateY(pFace.getPosition().y + face.getHeight() / 2);

        float midx = (x + px) / 2;
        float midy = (y + py) / 2;

        float movedx = x - midx;
        float movedy = y - midy;
        if(face.getLandmarks().size() > 1) {
            //Log.d("LandMarks: ", "" + face.getLandmarks().size());
            Landmark leftMLandmark = null;
            Landmark rightMLandmark = null;

            //boolean hasMouth = false;

            /*for(int i = 0;i < face.getLandmarks().size(); i++){
                Log.d("LandMarks: ", "" + face.getLandmarks().get(i).getType());
                if (face.getLandmarks().get(i).getType() == 5){
                    leftMLandmark = face.getLandmarks().get(i);
                    canvas.drawCircle(translateX( leftMLandmark.getPosition().x ), translateY(leftMLandmark.getPosition().y) , FACE_POSITION_RADIUS, mFacePositionPaint);
                    //hasMouth = true;
                }//left mouth
                if (face.getLandmarks().get(i).getType() == 11){
                    rightMLandmark = face.getLandmarks().get(i);
                    canvas.drawCircle(translateX( rightMLandmark.getPosition().x ) , translateY(rightMLandmark.getPosition().y) , FACE_POSITION_RADIUS, mFacePositionPaint);
                    //hasMouth = true;
                }//right mouth

                if (face.getLandmarks().get(i).getType() == 0){
                    bottomMLandmark = face.getLandmarks().get(i);
                    canvas.drawCircle(translateX( bottomMLandmark.getPosition().x ) , translateY(bottomMLandmark.getPosition().y) , FACE_POSITION_RADIUS, mFacePositionPaint);
                    canvas.drawText(mSpeechText,translateX( bottomMLandmark.getPosition().x ) , translateY(bottomMLandmark.getPosition().y) + ID_Y_OFFSET, mIdPaint);
                    //hasMouth = true;
                }//bottom mouth
            }*/
            float bx = 0;
            float by = 0;
            if(face.getLandmarks().get(face.getLandmarks().size()-1).getType() == 0){
                //Log.d("LandMarks: ", "caught" );
                bottomMLandmark = face.getLandmarks().get(face.getLandmarks().size()-1);
                bx = translateX( bottomMLandmark.getPosition().x );
                by =  translateY(bottomMLandmark.getPosition().y);
                canvas.drawCircle(bx , by, FACE_POSITION_RADIUS, mFacePositionPaint);
                canvas.drawText(mSpeechText,bx , by + ID_Y_OFFSET, mIdPaint);
            }
            else{
                if(bx != 0.0 && by != 0) {
                    canvas.drawCircle(bx + movedx, by + movedy, FACE_POSITION_RADIUS, mFacePositionPaint);
                    canvas.drawText(mSpeechText, bx + movedx, by + movedy + ID_Y_OFFSET, mIdPaint);
                    Log.d("LandMarks: ", "" +
                            "" + face.getLandmarks().get(face.getLandmarks().size() - 1).getType());
                }
            }
            //Log.d("LandMarks: ", "end" );
        }
        canvas.drawCircle(x , y ,FACE_POSITION_RADIUS,mFacePositionPaint);
        //Log.d("LandMarks: ", "" + face.getLandmarks());

        //canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
        //canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
        //canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET*2, y - ID_Y_OFFSET*2, mIdPaint);

        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, mBoxPaint);
    }
}
