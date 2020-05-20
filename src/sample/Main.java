package sample;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

class mayinliAraziOyunu extends JFrame {
    // Oyun mod numaraları
    public static final int RastgeleModu = 0;
    public static final int Basit_Mod = 1;
    public static final int OrtaSeviye_Mod = 2;
    public static final int SuperOyuncu_Mod = 3;
    // Oyun Modları
    public static final int[] Basit = {9, 9, 10, Basit_Mod};
    public static final int[] OrtaSeviye = {16, 16, 40, OrtaSeviye_Mod};
    public static final int[] SuperOyuncu = {16, 30, 99, SuperOyuncu_Mod};

    private static final Font CenturyFont = new Font("Century Gothic", Font.PLAIN, 13);
    private static final ImageIcon imagelogpic = new ImageIcon(mayinliAraziOyunu.class.getResource("/Images/logpic.gif"));
    private static final ImageIcon imBayrak = new ImageIcon(mayinliAraziOyunu.class.getResource("/Images/bayrak.gif"));
    private static final ImageIcon imMayin = new ImageIcon(mayinliAraziOyunu.class.getResource("/Images/bomba.gif"));
    private static final Color[] RnkPaleti = {Color.blue, Color.green, Color.orange, Color.magenta, Color.red, Color.pink,
            Color.black, Color.gray};

    private static File file = new File("mayinlitarlam.txt");
    private static int Basit_Seviye_Puan;
    private static int Orta_Seviye_Puan;
    private static int SuperOyuncu_Seviye_Puan;
    private int oyunMod;
    private int satirS;
    private int sutunS;
    private int alan;
    private int mynSayisi;
    private int byrkSayisi;
    private int etknMayn;
    private boolean[][] alnMayn;
    private boolean[][] byrkSimge;
    private boolean boOynBitti;
    private boolean boTik;
    private zamanThrd cekirdek;
    private JPanel pnlCntnt;
    private JMenuBar jmenubar;
    private JPanel pnlCnt;
    private JPanel pnl3;
    private MaynBtn[][] MayinButon;
    private JTextField tetFieldSure;
    private JTextField TetFieldSayi;

    public static void main(String[] args) {
        int[] gDF = getDF();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new mayinliAraziOyunu(gDF[0], gDF[1], gDF[2], gDF[3]);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public mayinliAraziOyunu(int satir, int sutun, int mSa, int mod) {
        setTitle("Mayınlı Tarla Oyunu");
        setResizable(false); // pencere boyutunu değiştirememe
        setVisible(true); // Pencereyi görünecek şekil ayarlama
        setIconImage(imagelogpic.getImage()); // Form simgesi
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Pencere kapanma olayları
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                kayitD();
            }
        });
        jmenubar = new JMenuBar();
        setJMenuBar(jmenubar);
        JMenu menuGameItem = new JMenu("Oyun");
        jmenubar.add(menuGameItem);
        JMenu menuYeni = new JMenu("Yeni");
        menuGameItem.add(menuYeni);
        JMenuItem menuYenidenItem = new JMenuItem("Oyunu Tekrarla");
        menuYenidenItem.addActionListener(new RestartListener());
        menuYeni.add(menuYenidenItem);
        JMenuItem menuYeniItem = new JMenuItem("Yeni Oyun");
        menuYeniItem.addActionListener(new ResetMineListener());
        menuYeni.add(menuYeniItem);

        menuGameItem.addSeparator();

        JMenuItem menuBasitItem = new JMenuItem("Basit");
        menuBasitItem.addActionListener(new ResetModeListener(Basit));
        menuGameItem.add(menuBasitItem);

        JMenuItem menuOrtaSvyItem = new JMenuItem("OrtaSeviye");
        menuOrtaSvyItem.addActionListener(new ResetModeListener(OrtaSeviye));
        menuGameItem.add(menuOrtaSvyItem);

        JMenuItem menuSuperGamerItem = new JMenuItem("Süper Oyuncu");
        menuSuperGamerItem.addActionListener(new ResetModeListener(SuperOyuncu));
        menuGameItem.add(menuSuperGamerItem);

        menuGameItem.addSeparator();

        JMenuItem menuKullaniciItem = new JMenuItem("Custom");
        menuKullaniciItem.addActionListener(new kullaniciTercih());
        menuGameItem.add(menuKullaniciItem);

        menuGameItem.addSeparator();

        JMenuItem menuYuksekSkrItem = new JMenuItem("Best Times..");
        menuYuksekSkrItem.addActionListener(new ySkorListe());
        menuGameItem.add(menuYuksekSkrItem);

        menuGameItem.addSeparator();

        JMenuItem menuKapayItem = new JMenuItem("Çıkış");


        menuKapayItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                kayitD();
                System.exit(0);
            }
        });

        menuGameItem.add(menuKapayItem);
        JMenu menuHelpItem = new JMenu("Help");
        menuHelpItem.add(new JMenuItem("https://github.com/erenalpt/mayinliAraziOyunum"));
        jmenubar.add(menuHelpItem);
        pnlCntnt = new JPanel();
        pnlCntnt.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlCntnt.setLayout(new BorderLayout(0, 0));
        setContentPane(pnlCntnt);
        pnlCnt = new JPanel();
        pnlCntnt.add(pnlCnt, BorderLayout.NORTH);
        JLabel lblTime = new JLabel("Süre");
        pnlCnt.add(lblTime);
        tetFieldSure = new JTextField("0", 3);
        tetFieldSure.setEditable(false);
        pnlCnt.add(tetFieldSure);
        JLabel lblCount = new JLabel("Bayrak");
        pnlCnt.add(lblCount);
        TetFieldSayi = new JTextField("0/" + mod, 5);
        TetFieldSayi.setEditable(false);
        pnlCnt.add(TetFieldSayi);
        pnl3 = new JPanel();
        pnlCntnt.add(pnl3, BorderLayout.CENTER);
        oyunMod = mod;
        stMaynArazi(satir, sutun, mSa);
        stMaynKnm();
        stMaynBtn();

    }


    public static int[] getDF() {
        try {
            FileInputStream fileInSt = new FileInputStream(file);
            byte[] dtd = new byte[256];
            int boyut = fileInSt.read(dtd);
            fileInSt.close();
            String[] sDtD = new String(dtd, 0, boyut).split(" ");
            Basit_Seviye_Puan = Integer.parseInt(sDtD[0]);
            Orta_Seviye_Puan = Integer.parseInt(sDtD[1]);
            SuperOyuncu_Seviye_Puan = Integer.parseInt(sDtD[2]);
            int[] deger = {Integer.parseInt(sDtD[3]), Integer.parseInt(sDtD[4]), Integer.parseInt(sDtD[5]),
                    Integer.parseInt(sDtD[6])};
            return deger;
        } catch (Exception ex) {
            Basit_Seviye_Puan = Integer.MAX_VALUE;
            Orta_Seviye_Puan = Integer.MAX_VALUE;
            SuperOyuncu_Seviye_Puan = Integer.MAX_VALUE;
            return Basit;
        }
    }

    public void kayitD() {
        try {
            String dege = Basit_Seviye_Puan + " " + Orta_Seviye_Puan + " " + SuperOyuncu_Seviye_Puan + " " + satirS + " "
                    + sutunS + " " + mynSayisi + " " + oyunMod;
            FileOutputStream fileOuSt = new FileOutputStream(file);
            fileOuSt.write(dege.getBytes());
            fileOuSt.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void stMaynArazi(int satir, int sutun, int mSa) {
        setSize(30 * sutun + 20, 30 * satir + 120);
        setLocationRelativeTo(null);
        this.satirS = satir;
        this.sutunS = sutun;
        this.mynSayisi = mSa;
        alan = satir * sutun;
        byrkSayisi = 0;
        etknMayn = alan;
        alnMayn = new boolean[satir][sutun];
        byrkSimge = new boolean[satir][sutun];
        boOynBitti = false;
        boTik = true;
    }

    //Rastgele üretilen bomba konumları
    private void stMaynKnm() {
        int[] pozisyon = new int[alan];
        int i = 0;
        while (i < alan) {
            pozisyon[i] = i;
            i++;
        }

        Random rnd = new Random();
        int aa = 0;
        while (aa < mynSayisi) {
            int rd = rnd.nextInt(alan - aa);
            alnMayn[pozisyon[rd] / sutunS][pozisyon[rd] % sutunS] = true;
            pozisyon[rd] = pozisyon[alan - aa - 1];
            aa++;
        }
    }

    // Mayın paneli
    private void stMaynBtn() {
        pnl3.setLayout(new GridLayout(satirS, sutunS));
        MayinButon = new MaynBtn[satirS][sutunS];

        for (int i = 0; i < satirS; i++) {
            for (int j = 0; j < sutunS; j++) {
                MayinButon[i][j] = new MaynBtn(i, j);
                pnl3.add(MayinButon[i][j]);
            }
        }
    }

    // Varolan oyunun devam etmesi olayı
    private class RestartListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            new mMetot().oyunTekrar();
        }
    }

    // Yeni bir oyun başlatma olayı
    private class ResetMineListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            new mMetot().oyunSifirla();
        }
    }

    // Oyun modu ayarlarının olayları
    private class ResetModeListener implements ActionListener {
        private int[] oyunModu;

        public ResetModeListener(int[] mode) {
            this.oyunModu = mode;
        }

        public void actionPerformed(ActionEvent e) {
            if (oyunMod == oyunModu[3]) {
                new mMetot().oyunSifirla();
            } else {
                new mMetot().boyutSifirla(oyunModu[0], oyunModu[1], oyunModu[2]);
                oyunMod = oyunModu[3];
            }
        }
    }


    //Custom olarak ayarlama olayları
    private class kullaniciTercih extends JFrame implements ActionListener {
        public kullaniciTercih() {// yapıcı metot
            setTitle("Kullanici Tercih");
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            setSize(320, 220);
            setLocationRelativeTo(null);// Pencere ekranın ortasına yerleşsin
            setResizable(false); // Form boyutunun değiştirilememesi
            JPanel jpanelCp = new JPanel();
            jpanelCp.setBorder(new EmptyBorder(7, 7, 7, 7));
            jpanelCp.setLayout(new GridLayout(4, 0));
            setContentPane(jpanelCp);
            JPanel jpanelSatir = new JPanel();
            jpanelCp.add(jpanelSatir);
            jpanelSatir.add(new JLabel("Satır Sayısı:"));
            JTextField tetFldSatir = new JTextField(5);
            jpanelSatir.add(tetFldSatir);
            JPanel jpanelSutun = new JPanel();
            jpanelCp.add(jpanelSutun);
            jpanelSutun.add(new JLabel("Sütun Sayısı:"));
            JTextField tetFldSutun = new JTextField(5);
            jpanelSutun.add(tetFldSutun);
            JPanel jpanelMaynS = new JPanel();
            jpanelCp.add(jpanelMaynS);
            jpanelMaynS.add(new JLabel("Bomba Sayısı:"));
            JTextField tetFldMaynS = new JTextField(5);
            jpanelMaynS.add(tetFldMaynS);
            JPanel jpanelBtn = new JPanel();
            jpanelCp.add(jpanelBtn);
            JButton btnOK = new JButton("OK");
            btnOK.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        int FldSatir = Integer.parseInt(tetFldSatir.getText().trim());
                        int FldSutun = Integer.parseInt(tetFldSutun.getText().trim());
                        int FldM = Integer.parseInt(tetFldMaynS.getText().trim());
                        if (FldSatir <= 0 || FldSutun <= 0 || FldSatir > 23 || FldSutun > 70) {
                            JOptionPane.showMessageDialog(null, "Satır Sayısı：1 - 23\nSütun Sayısı：1 - 70", "UYARI",
                                    JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        if (FldM > FldSatir * FldSutun || FldM <= 0) {
                            JOptionPane.showMessageDialog(null, "Limitleri Zorlama：1 " + (FldSatir * FldSutun), "UYARI", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        new mMetot().boyutSifirla(FldSatir, FldSutun, FldM);
                        oyunMod = RastgeleModu;
                        setVisible(false);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Geçersiz Giriş!", "HATA", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            jpanelBtn.add(btnOK);
        }

        public void actionPerformed(ActionEvent e) {
            setVisible(true);
        }
    }

    // Yüksek skorlar olayı
    private class ySkorListe implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null,
                    "Birinci:" + Basit_Seviye_Puan + "Saniye\nAra: " + Orta_Seviye_Puan + "İkinci\nİleri Düzey: "
                            + SuperOyuncu_Seviye_Puan + "İkinci",
                    "Yüksek Puan Listesi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Menü çubuğu İslevleri
    private class mMetot {
        // Varolan oyunu devam ettirme fonksiyonu
        public void oyunTekrar() {
            byrkSayisi = 0;
            etknMayn = alan;
            if (!boTik) {
                cekirdek.interrupt();
                boTik = true;
                tetFieldSure.setText("0");
            }
            TetFieldSayi.setText("0/" + mynSayisi);


            for (int i = 0; i < satirS; i++) {
                for (int j = 0; j < sutunS; j++) {
                    byrkSimge[i][j] = false;
                    MayinButon[i][j].lblTemizle();
                    MayinButon[i][j].setIcon(null);
                    MayinButon[i][j].setOriginalStyle();
                    MayinButon[i][j].setEnabled(true);
                }
            }


            if (boOynBitti) {
                for (int i = 0; i < satirS; i++) {
                    for (int j = 0; j < sutunS; j++) {
                        MayinButon[i][j].setMaynTkp();
                    }
                }


                boOynBitti = false;
            }
        }

        // yeni oyun başlat fonksiyonu
        public void oyunSifirla() {
            alnMayn = new boolean[satirS][sutunS];
            stMaynKnm();
            oyunTekrar();
        }

        // Oyun modundaki ayarların işlemleri
        public void boyutSifirla(int satir, int sutun, int mSa) {
            oyunTekrar();
            TetFieldSayi.setText("0/" + mSa);
            pnl3.removeAll();
            stMaynArazi(satir, sutun, mSa);
            stMaynKnm();
            stMaynBtn();
        }
    }

    // Mayın tarlasındaki mouse izleme
    private class MaynTakip extends MouseAdapter {
        private int strS;
        private int stnS;
        private boolean ciftTiklama;
        private mTik tiklaMethod;

        public MaynTakip(int strs, int stns) {
            this.strS = strs;
            this.stnS = stns;
            tiklaMethod = new mTik(strs, stns);
        }

        // Fare tıklandığında çalışacak olay
        public void mousePressed(MouseEvent em) {
            // hem sağ hem sol tuşa tıklanıyor mu ?
            int exMod = em.getModifiersEx();
            ciftTiklama = exMod == InputEvent.BUTTON1_DOWN_MASK + InputEvent.BUTTON3_DOWN_MASK;
            if (ciftTiklama) {
                tiklaMethod.ciftTik();
            }
        }

        // Fare bırakıldığında patlama olayı
        public void mouseReleased(MouseEvent em) {
            if (ciftTiklama) {
                tiklaMethod.ciftTikDrm();
            } else if (em.getButton() == MouseEvent.BUTTON1) {
                tiklaMethod.sol(strS, stnS);
            } else if (em.getButton() == MouseEvent.BUTTON3 && !ciftTiklama) {
                tiklaMethod.sag();
            }
            if (mynSayisi == etknMayn) {
                tiklaMethod.yendin();
            }
        }
    }

    //Mayın alanındaki mouse fonksiyonları
    private class mTik {
        private int strS;
        private int stnS;
        private boolean[] varMi;

        public mTik(int strs, int stns) {
            this.strS = strs;
            this.stnS = stns;
            varMi = cvBtn(strs, stns);
        }

        // Tıklanan yer etrafında 3*3 başka bomba var mı
        private boolean[] cvBtn(int strs, int stns) {
            boolean[] varmi = {true, true, true, true, true, true, true, true, true};
            if (strs == 0) {
                varmi[0] = varmi[1] = varmi[2] = false;
            }
            if (strs == satirS - 1) {
                varmi[6] = varmi[7] = varmi[8] = false;
            }
            if (stns == 0) {
                varmi[0] = varmi[3] = varmi[6] = false;
            }
            if (stns == sutunS - 1) {
                varmi[2] = varmi[5] = varmi[8] = false;
            }
            return varmi;
        }

        // Sol tiklama olacak işlem fonksiyonu
        public void sol(int strs, int stns) {
            if (boTik) {
                cekirdek = new zamanThrd();
                cekirdek.start();
                boTik = false;
            }
            if (byrkSimge[strs][stns] || !MayinButon[strs][stns].isEnabled()) {
                return;
            }
            if (alnMayn[strs][stns]) {
                yenildin(strs, stns);
                return;
            }
            boolean[] varmi = cvBtn(strs, stns);
            int i = 0;
            if (varmi[0] && alnMayn[strs - 1][stns - 1]) {
                i++;
            }
            if (varmi[1] && alnMayn[strs - 1][stns]) {
                i++;
            }
            if (varmi[2] && alnMayn[strs - 1][stns + 1]) {
                i++;
            }
            if (varmi[3] && alnMayn[strs][stns - 1]) {
                i++;
            }
            if (varmi[5] && alnMayn[strs][stns + 1]) {
                i++;
            }
            if (varmi[6] && alnMayn[strs + 1][stns - 1]) {
                i++;
            }
            if (varmi[7] && alnMayn[strs + 1][stns]) {
                i++;
            }
            if (varmi[8] && alnMayn[strs + 1][stns + 1]) {
                i++;
            }
            MayinButon[strs][stns].setDisabledStyle();
            MayinButon[strs][stns].setEnabled(false);
            etknMayn--;
            if (i != 0) {
                MayinButon[strs][stns].stLbl(i);
            } else {
                if (varmi[0]) {
                    sol(strs - 1, stns - 1);
                }
                if (varmi[1]) {
                    sol(strs - 1, stns);
                }
                if (varmi[2]) {
                    sol(strs - 1, stns + 1);
                }
                if (varmi[3]) {
                    sol(strs, stns - 1);
                }
                if (varmi[5]) {
                    sol(strs, stns + 1);
                }
                if (varmi[6]) {
                    sol(strs + 1, stns - 1);
                }
                if (varmi[7]) {
                    sol(strs + 1, stns);
                }
                if (varmi[8]) {
                    sol(strs + 1, stns + 1);
                }
            }
        }

        // Sağ tiklama bayrak işlevi fonksiyonu
        public void sag() {
            if (byrkSimge[strS][stnS]) {
                MayinButon[strS][stnS].setIcon(null);
                byrkSimge[strS][stnS] = false;
                byrkSayisi--;
                TetFieldSayi.setText(byrkSayisi + "/" + mynSayisi);
            } else {
                if (MayinButon[strS][stnS].isEnabled()) {
                    MayinButon[strS][stnS].setIcon(imBayrak);
                    byrkSimge[strS][stnS] = true;
                    byrkSayisi++;
                    TetFieldSayi.setText(byrkSayisi + "/" + mynSayisi);
                }
            }
        }

        // Çift tıklama işlevi fonksiyonu
        public void ciftTik() {
            if (byrkSimge[strS][stnS]) {
                return;
            }
            int k = 0;
            int ii = -1;
            int jj = -1;
            while (ii <= 1) {
                while (jj <= 1) {
                    if (varMi[k++] && MayinButon[strS + ii][stnS + jj].isEnabled()
                            && !byrkSimge[strS + ii][stnS + jj]) {
                        MayinButon[strS + ii][stnS + jj].setClickedStyle();
                    }
                    jj++;
                }
                ii++;
            }
        }

        // Çift tıklama işlevinden sonraki fonksiyonu
        public void ciftTikDrm() {
            if (byrkSimge[strS][stnS]) {
                return;
            }
            int k = 0, m = 0, n = 0;
            byte iii = -1;
            byte jjj = -1;
            while (iii <= 1) {
                while (jjj <= 1) {
                    if (varMi[k++]) {
                        if (MayinButon[strS + iii][stnS + jjj].isEnabled() && !byrkSimge[strS + iii][stnS + jjj]) {
                            MayinButon[strS + iii][stnS + jjj].setOriginalStyle();
                        }
                        if (alnMayn[strS + iii][stnS + jjj]) {
                            m++;
                        }
                        if (byrkSimge[strS + iii][stnS + jjj]) {
                            n++;
                        }
                    }
                    jjj++;
                }
                iii++;
            }

            if (MayinButon[strS][stnS].isEnabled()) {
                return;
            } // dikkatttt
            if (m == n) {
                if (varMi[0] && !byrkSimge[strS - 1][stnS - 1]) {
                    sol(strS - 1, stnS - 1);
                }
                if (varMi[1] && !byrkSimge[strS - 1][stnS]) {
                    sol(strS - 1, stnS);
                }
                if (varMi[2] && !byrkSimge[strS - 1][stnS + 1]) {
                    sol(strS - 1, stnS + 1);
                }
                if (varMi[3] && !byrkSimge[strS][stnS - 1]) {
                    sol(strS, stnS - 1);
                }
                if (varMi[5] && !byrkSimge[strS][stnS + 1]) {
                    sol(strS, stnS + 1);
                }
                if (varMi[6] && !byrkSimge[strS + 1][stnS - 1]) {
                    sol(strS + 1, stnS - 1);
                }
                if (varMi[7] && !byrkSimge[strS + 1][stnS]) {
                    sol(strS + 1, stnS);
                }
                if (varMi[8] && !byrkSimge[strS + 1][stnS + 1]) {
                    sol(strS + 1, stnS + 1);
                }
            }
        }

        // Oyun kaybedilince çalışacak olaylar.
        public void yenildin(int strs, int stns) {
            MayinButon[strs][stns].setBackground(Color.red);
            for (int i = 0; i < satirS; i++) {
                for (int j = 0; j < sutunS; j++) {
                    if (alnMayn[i][j]) {
                        MayinButon[i][j].setIcon(imMayin);
                    }
                    MayinButon[i][j].removeMineListener();
                }
            }
            cekirdek.interrupt();
            JOptionPane.showMessageDialog(null, "Üzgünüm. Oyunu Kaybettin", "GAME OVER", JOptionPane.INFORMATION_MESSAGE);
            boOynBitti = true;
        }

        // basarili
        public void yendin() {
            TetFieldSayi.setText(mynSayisi + "/" + mynSayisi);
            int ii = 0;
            int jj = 0;
            while (ii < satirS) {
                while (jj < sutunS) {
                    if (alnMayn[ii][jj]) {
                        MayinButon[ii][jj].setIcon(imBayrak);
                    }
                    MayinButon[ii][jj].removeMineListener();
                    jj++;
                }
                ii++;
            }


            if (cekirdek != null) {
                cekirdek.interrupt();
            }
            String sonuc = "KAZANDIN!";
            int sure = Integer.parseInt(tetFieldSure.getText());
            switch (oyunMod) {
                // Exper modda yeni rekor gösterimi
                case Basit_Mod:
                    if (sure < Basit_Seviye_Puan) {
                        Basit_Seviye_Puan = sure;
                        sonuc = "Süper Rekor!";
                    }
                    break;
                case OrtaSeviye_Mod:
                    if (sure < Orta_Seviye_Puan) {
                        Orta_Seviye_Puan = sure;
                        sonuc = "Süper Rekor!";
                    }
                    break;
                case SuperOyuncu_Mod:
                    if (sure < SuperOyuncu_Seviye_Puan) {
                        SuperOyuncu_Seviye_Puan = sure;
                        sonuc = "Süper Rekor!";
                    }
            }
            JOptionPane.showMessageDialog(null, sonuc + "\nGeçen Süre: " + sure + " ", "KAZANDIN!", JOptionPane.INFORMATION_MESSAGE);
            boOynBitti = true;
        }
    }

    //button alt sınıfı
    private class MaynBtn extends JButton {
        private MaynTakip maynTkp;
        private JLabel jlabell;

        private MaynBtn() {
            super(null, null);
            setBackground(Color.cyan);
            setBorder(BorderFactory.createRaisedBevelBorder());
        }

        // Mayın Butonlar
        public MaynBtn(int strs, int stns) {
            this();
            maynTkp = new MaynTakip(strs, stns);
            addMouseListener(maynTkp);
        }

        public void setMaynTkp() {
            addMouseListener(maynTkp);
        }

        public void removeMineListener() {
            removeMouseListener(maynTkp);
        }

        public void setOriginalStyle() {
            setBackground(Color.cyan);
            setBorder(BorderFactory.createRaisedBevelBorder());
        }

        public void setDisabledStyle() {
            setBackground(null);
            setBorder(BorderFactory.createLineBorder(Color.cyan));
        }

        public void setClickedStyle() {
            setBackground(Color.black);
            setBorder(BorderFactory.createLoweredBevelBorder());
        }

        public void stLbl(int i) {
            jlabell = new JLabel(String.valueOf(i));
            jlabell.setHorizontalAlignment(JLabel.CENTER);
            jlabell.setVerticalAlignment(JLabel.CENTER);
            jlabell.setForeground(RnkPaleti[i - 1]);
            setLayout(new BorderLayout(0, 0));
            add(jlabell, BorderLayout.CENTER);
        }

        public void lblTemizle() {
            if (jlabell != null) {
                remove(jlabell);
                jlabell = null;
            }
        }
    }

    private class zamanThrd extends Thread {
        public void run() {

            long baslamaZamani = System.currentTimeMillis();

            while (!isInterrupted()) {
//Geçerli sistem saatini al, başlangıç zamanından çıkart ve 1000 e böl. saniyeyi hesapla
                long bitisZamani = System.currentTimeMillis();
                long kullanilanZaman = (bitisZamani - baslamaZamani) / 1000;
                tetFieldSure.setText(String.valueOf(kullanilanZaman));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    break;
                }
            }
        }
    }
}
