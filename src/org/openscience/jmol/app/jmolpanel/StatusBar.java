/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2009-06-30 18:58:33 -0500 (Tue, 30 Jun 2009) $
 * $Revision: 11158 $
 *
 * Copyright (C) 2002-2005  The Jmol Development Team
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
package org.openscience.jmol.app.jmolpanel;

import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class StatusBar extends JPanel {

  protected JLabel[] status;

  public static final int STATUS_COORD  = 1;
  public static final int STATUS_TEXT   = 2;
  public static final int STATUS_TIME   = 3;

  public StatusBar(int startupWidth) {
    setPreferredSize(new Dimension(startupWidth, 30));
    status = new JLabel[3];
    setLayout(new GridLayout(1, 3));
    status[0] = new JLabel();
    status[0].setPreferredSize(new Dimension(100, 100));
    status[0].setBorder(BorderFactory.createBevelBorder(1));
    status[0].setHorizontalAlignment(0);
    status[0].setOpaque(true);
    status[1] = new JLabel();
    status[1].setPreferredSize(new Dimension(100, 100));
    status[1].setBorder(BorderFactory.createBevelBorder(1));
    status[1].setHorizontalAlignment(0);
    status[1].setOpaque(true);
    status[2] = new JLabel();
    status[2].setPreferredSize(new Dimension(100, 100));
    status[2].setBorder(BorderFactory.createBevelBorder(1));
    status[2].setFont(new Font("Monospaced", Font.PLAIN, 12));
    status[2].setHorizontalAlignment(0);
    status[2].setOpaque(true);
    add(status[0]);
    add(status[1]);
    add(status[2]);
  }

  public void setStatus(int pos, String text) {
    status[pos - 1].setText(text);
  }
}
