package tb_client;

import entities.seats;
import java.awt.Color;
import javax.swing.JOptionPane;

/**
 *
 * @author Aryan Mehta
 */
public class seat_panel extends javax.swing.JPanel {

    private seats s;
    private Client cl;

    private boolean enabled;

    public seat_panel(seats s, Client cl) {
        initComponents();
        this.s = s;
        this.cl = cl;
        init();
    }

    private void init() {
        lbl_seatNum.setText(s.getSeat_number());
        if (s.getSeat_status().equals("NOT_BOOKED")) {
            lbl_seatNum.setBackground(Color.GREEN);
            enabled = true;
        } else {
            lbl_seatNum.setBackground(Color.RED);
            enabled = false;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_seatNum = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lbl_seatNum.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbl_seatNum.setForeground(new java.awt.Color(255, 255, 255));
        lbl_seatNum.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_seatNum.setOpaque(true);
        lbl_seatNum.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_seatNumMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_seatNum, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_seatNum, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void lbl_seatNumMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_seatNumMouseClicked
        if (enabled) {
            int res = cl.checkSeatWriteStatus(s);
            if(res==1){
                new viewTicketFrame(s, cl).setVisible(true);
            }else{
                JOptionPane.showMessageDialog(null, "Seat is being Booked!!");
            }
        }else{
            JOptionPane.showMessageDialog(null, "Seat Already Booked!!");
        }
    }//GEN-LAST:event_lbl_seatNumMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lbl_seatNum;
    // End of variables declaration//GEN-END:variables
}
