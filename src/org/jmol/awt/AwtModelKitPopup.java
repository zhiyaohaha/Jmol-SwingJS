/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-05-11 15:47:18 -0500 (Tue, 11 May 2010) $
 * $Revision: 13064 $
 *
 * Copyright (C) 2000-2005  The Jmol Development Team
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
package org.jmol.awt;

import java.awt.Component;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.jmol.api.SC;
import org.jmol.modelkit.ModelKitPopup;
import org.jmol.popup.PopupResource;

public class AwtModelKitPopup extends ModelKitPopup {

  public AwtModelKitPopup() {
    helper = new AwtPopupHelper(this);
  }
  
  @Override
  protected void addMenu(String id, String item, SC subMenu, String label,
                         PopupResource popupResourceBundle) {
    
    AwtSwingComponent c = (AwtSwingComponent) subMenu;
    c.deferred = true;
    c.jm.addMenuListener(new MenuListener() {

      @Override
      public void menuSelected(MenuEvent e) {
        if (c.deferred) {
          c.deferred = false;
          if (item.indexOf("Computed") < 0)
            addMenuItems(id, item, subMenu, popupResourceBundle);
          updateAwtMenus(item);        
        }
      }

      @Override
      public void menuDeselected(MenuEvent e) {
      }

      @Override
      public void menuCanceled(MenuEvent e) {
      }
      
    });
    }


  protected void updateAwtMenus(String item) {
    System.out.println("Awtmodelkitpopup "  + item);
    switch (item) {
    case "xtalOp!PersistMenu":
      lastModelSet = null;
      jpiUpdateComputedMenus();
      break;
    default:
      updateOperatorMenu();
      break;
    }
  }

  @Override
  protected void menuShowPopup(SC popup, int x, int y) {
    try {
      ((JPopupMenu)((AwtSwingComponent)popup).jc).show((Component) vwr.display, x, y);
    } catch (Exception e) {
      // ignore
    }
  }

  @Override
  protected Object getImageIcon(String fileName) {
    String imageName = "org/jmol/modelkit/images/" + fileName;
    URL imageUrl = this.getClass().getClassLoader().getResource(imageName);
    return (imageUrl == null ? null : new ImageIcon(imageUrl));
  }

}
