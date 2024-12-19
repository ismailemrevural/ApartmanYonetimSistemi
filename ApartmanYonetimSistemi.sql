CREATE TABLE Kullanicilar (
    KullaniciID INT AUTO_INCREMENT PRIMARY KEY,
    Ad VARCHAR(50) NOT NULL,
    Soyad VARCHAR(50) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    Telefon VARCHAR(15),
    Sifre VARCHAR(100) NOT NULL,
    Rol ENUM('Yonetici', 'Sakin') DEFAULT 'Sakin'
);

CREATE TABLE Aidatlar (
    AidatID INT AUTO_INCREMENT PRIMARY KEY,
    DaireID INT NOT NULL,
    Tutar DECIMAL(10, 2) NOT NULL,
    SonOdemeTarihi DATE NOT NULL,
    OdemeTarihi DATE,
    Durum ENUM('Odenmedi', 'Odendi') DEFAULT 'Odenmedi',
    FOREIGN KEY (DaireID) REFERENCES Daireler(DaireID) ON DELETE CASCADE
);

CREATE TABLE BakimOnarimlar (
    IslemID INT AUTO_INCREMENT PRIMARY KEY,
    Konu VARCHAR(100) NOT NULL,
    Aciklama TEXT,
    Fiyat DECIMAL(10, 2),
    Tarih DATE NOT NULL
);

CREATE TABLE Bildirimler (
    BildirimID INT AUTO_INCREMENT PRIMARY KEY,
    GonderenID INT, -- Bildirimi gönderen kullanıcı (örneğin, yönetici)
    AliciID INT,    -- Bildirimi alan kullanıcı (NULL ise genel duyuru olarak kabul edilir)
    Baslik VARCHAR(100), -- Bildirim başlığı
    Icerik TEXT NOT NULL, -- Bildirim içeriği
    GonderimTarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (GonderenID) REFERENCES Kullanicilar(KullaniciID) ON DELETE SET NULL,
    FOREIGN KEY (AliciID) REFERENCES Kullanicilar(KullaniciID) ON DELETE CASCADE
);

-- Bir tane apartman olacak
CREATE TABLE Apartman (
    ApartmanID INT AUTO_INCREMENT PRIMARY KEY, -- Apartman kimliği
    Ad VARCHAR(100) NOT NULL,                  -- Apartman adı
    KatSayisi INT NOT NULL,                    -- Apartmandaki toplam kat sayısı
    DaireSayisi INT NOT NULL,                  -- Apartmandaki toplam daire sayısı
    Adres TEXT NOT NULL,                       -- Apartman adresi
    YoneticiID INT,                            -- Apartman yöneticisi
    FOREIGN KEY (YoneticiID) REFERENCES Kullanicilar(KullaniciID) ON DELETE SET NULL
);

CREATE TABLE Bloklar (
    BlokID INT AUTO_INCREMENT PRIMARY KEY,     -- Blok kimliği
    ApartmanID INT NOT NULL,                   -- Hangi apartmana bağlı olduğu
    BlokAdi VARCHAR(10) NOT NULL,              -- Blok adı (örneğin, "A Blok")
    KatSayisi INT NOT NULL,                    -- Bu bloktaki toplam kat sayısı
    FOREIGN KEY (ApartmanID) REFERENCES Apartman(ApartmanID) ON DELETE CASCADE
);

CREATE TABLE DaireTipleri (
    TipID INT AUTO_INCREMENT PRIMARY KEY,
    TipAdi VARCHAR(20) NOT NULL,   -- Örneğin "2+1", "3+1" gibi
    ApartmanID INT NOT NULL,       -- Hangi apartmana ait olduğu
    FOREIGN KEY (ApartmanID) REFERENCES Apartman(ApartmanID) ON DELETE CASCADE
);

CREATE TABLE Daireler (
    DaireID INT AUTO_INCREMENT PRIMARY KEY,
    BlokID INT NOT NULL,         -- Hangi blokta
    TipID INT NOT NULL,          -- Hangi tipte
    KullaniciID INT,             -- Hangi kullanıcıda
    DaireNo VARCHAR(10) NOT NULL UNIQUE, -- Daire numarası
    KatNo INT NOT NULL,          -- Hangi katta
    FOREIGN KEY (BlokID) REFERENCES Bloklar(BlokID) ON DELETE CASCADE,
    FOREIGN KEY (TipID) REFERENCES DaireTipleri(TipID) ON DELETE RESTRICT,
    FOREIGN KEY (KullaniciID) REFERENCES Kullanicilar(KullaniciID) ON DELETE SET NULL
);
