/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2016-10-08 12:28:44 -0500 (Sat, 08 Oct 2016) $
 * $Revision: 21258 $
 *
 * Copyright (C) 2002-2006  Miguel, Jmol Development, www.jmol.org
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jmol.render;

import javajs.util.P3;

import org.jmol.script.T;
import org.jmol.shape.Axes;
import org.jmol.util.GData;
import org.jmol.viewer.StateManager;


public class AxesRenderer extends CageRenderer {

  private final static String[] axisLabels = { "+X", "+Y", "+Z", null, null, null, 
                                  "a", "b", "c", 
                                  "X", "Y", "Z", null, null, null,
                                  "X", null, "Z", null, "(Y)", null};

  private final P3 originScreen = new P3();
  
  private short[] colixes = new short[3];

  private P3 pt000;

  private final static String[] axesTypes = {"a", "b", "c"};

  @Override
  protected void initRenderer() {
    endcap = GData.ENDCAPS_FLAT; 
    draw000 = false;
  }

  @Override
  protected boolean render() {
    Axes axes = (Axes) shape;
    int mad10 = vwr.getObjectMad10(StateManager.OBJ_AXIS1);
    // no translucent axes
    if (mad10 == 0 || !g3d.checkTranslucent(false))
      return false;
    boolean isXY = (axes.axisXY.z != 0);
    if (!isXY && tm.isNavigating() && vwr.getBoolean(T.navigationperiodic))
      return false;
    imageFontScaling = vwr.imageFontScaling;
    if (vwr.areAxesTainted())
      axes.reinitShape();
    font3d = vwr.gdata.getFont3DScaled(axes.font3d, imageFontScaling);

    int modelIndex = vwr.am.cmi;
    boolean isDataFrame = vwr.isJmolDataFrame();
    pt000 = (isDataFrame ? pt0 : axes.originPoint);

    // includes check here for background model present
    boolean isUnitCell = (vwr.g.axesMode == T.axesunitcell);
    if (vwr.ms.isJmolDataFrameForModel(modelIndex)
        && !vwr.ms.getJmolFrameType(modelIndex).equals("plot data"))
      return false;
    if (isUnitCell && modelIndex < 0 && vwr.getCurrentUnitCell() == null)
      return false;
    int nPoints = 6;
    int labelPtr = 0;
    if (isUnitCell && ms.unitCells != null) {
      nPoints = 3;
      labelPtr = 6;
    } else if (isXY) {
      nPoints = 3;
      labelPtr = 9;
    } else if (vwr.g.axesMode == T.axeswindow) {
      nPoints = 6;
      labelPtr = (vwr.getBoolean(T.axesorientationrasmol) ? 15 : 9);
    }
    if (axes.labels != null) {
      if (nPoints != 3)
        nPoints = (axes.labels.length < 6 ? 3 : 6);
      labelPtr = -1;
    }

    int slab = vwr.gdata.slab;
    int diameter = mad10;
    boolean drawTicks = false;
    P3 ptTemp = originScreen;
    boolean checkAxisType = (axes.axisType != null && (isXY || vwr.getFloat(T.axesoffset) != 0 || axes.fixedOrigin != null));
    if (isXY) {
      if (exportType == GData.EXPORT_CARTESIAN)
        return false;
      if (mad10 >= 20) {
        // width given in angstroms as mAng.
        // max out at 500
        diameter = (mad10 > 500 ? 5 : mad10 / 100);
        if (diameter == 0)
          diameter = 2;
      } else {
        if (g3d.isAntialiased())
          diameter += diameter;
      }
      g3d.setSlab(0);
      ptTemp = axes.axisXY;
      pt0i.setT(tm.transformPt2D(ptTemp));
      if (ptTemp.x < 0) {
        // window origin
        int offx = (int) ptTemp.x;
        int offy = (int) ptTemp.x;
        // offset is from {0 0 0}
        pointT.setT(pt000);
        for (int i = 0; i < 3; i++)
          pointT.add(axes.getAxisPoint(i, false));
        pt0i.setT(tm.transformPt(pt000));
        pt2i.scaleAdd(-1, pt0i, tm.transformPt(pointT));
        if (pt2i.x < 0)
          offx = -offx;
        if (pt2i.y < 0)
          offy = -offy;
        pt0i.x += offx;
        pt0i.y += offy;
      }
      ptTemp = originScreen;
      ptTemp.set(pt0i.x, pt0i.y, pt0i.z);
      float zoomDimension = vwr.getScreenDim();
      float scaleFactor = zoomDimension / 10f * axes.scale;
      if (g3d.isAntialiased())
        scaleFactor *= 2;
      for (int i = 0; i < 3; i++) {
        P3 pt = p3Screens[i];
        tm.rotatePoint(axes.getAxisPoint(i, false), pt);
        pt.z *= -1;
        pt.scaleAdd2(scaleFactor, pt, ptTemp);
      }
    } else {
      drawTicks = (axes.tickInfos != null);
      if (drawTicks) {
        checkTickTemps();
        tickA.setT(pt000);
      }
      tm.transformPtNoClip(pt000, ptTemp);
      diameter = getDiameter((int) ptTemp.z, mad10);
      for (int i = nPoints; --i >= 0;)
        tm.transformPtNoClip(axes.getAxisPoint(i, isDataFrame), p3Screens[i]);
    }
    float xCenter = ptTemp.x;
    float yCenter = ptTemp.y;
    colixes[0] = vwr.getObjectColix(StateManager.OBJ_AXIS1);
    colixes[1] = vwr.getObjectColix(StateManager.OBJ_AXIS2);
    colixes[2] = vwr.getObjectColix(StateManager.OBJ_AXIS3);
    boolean showOrigin = (!isXY && nPoints == 3 && axes.scale == 2);
    for (int i = nPoints; --i >= 0;) {
      if (checkAxisType
          && !axes.axisType.contains(axesTypes[i])
          ||
          exportType != GData.EXPORT_CARTESIAN && 
          (Math.abs(xCenter - p3Screens[i].x)
              + Math.abs(yCenter - p3Screens[i].y) <= 2)
          && (!(showOrigin = false)) // setting showOrigin here
      )
        continue;
      colix = colixes[i % 3];
      g3d.setC(colix);
      String label = (axes.labels == null ? axisLabels[i + labelPtr]
          : i < axes.labels.length ? axes.labels[i] : null);
      if (label != null && label.length() > 0)
        renderLabel(label, p3Screens[i].x, p3Screens[i].y, p3Screens[i].z,
            xCenter, yCenter);
      if (drawTicks) {
        tickInfo = axes.tickInfos[(i % 3) + 1];
        if (tickInfo == null)
          tickInfo = axes.tickInfos[0];
        tickB.setT(axes.getAxisPoint(i, isDataFrame));
        if (tickInfo != null) {
          tickInfo.first = 0;
          tickInfo.signFactor = (i % 6 >= 3 ? -1 : 1);
        }
      }
      renderLine(ptTemp, p3Screens[i], diameter, drawTicks && tickInfo != null);
    }
    if (showOrigin) { // a b c [orig]
      String label0 = (axes.labels == null || axes.labels.length == 3
          || axes.labels[3] == null ? "0" : axes.labels[3]);
      if (label0 != null && label0.length() != 0) {
        colix = vwr.cm.colixBackgroundContrast;
        g3d.setC(colix);
        renderLabel(label0, xCenter, yCenter, ptTemp.z, xCenter, yCenter);
      }
    }
    if (isXY)
      g3d.setSlab(slab);
    return false;
  }
  
  private void renderLabel(String str, float x, float y, float z, float xCenter, float yCenter) {
    int strAscent = font3d.getAscent();
    int strWidth = font3d.stringWidth(str);
    float dx = x - xCenter;
    float dy = y - yCenter;
    if ((dx != 0 || dy != 0)) {
      float dist = (float) Math.sqrt(dx * dx + dy * dy);
      dx = (strWidth * 0.75f * dx / dist);
      dy = (strAscent * 0.75f * dy / dist);
      x += dx;
      y += dy;
    }
    double xStrBaseline = Math.floor(x - strWidth / 2f);
    double yStrBaseline = Math.floor(y + strAscent / 2f);
    g3d.drawString(str, font3d, (int) xStrBaseline, (int) yStrBaseline, (int) z, (int) z, (short) 0);
  }
}
