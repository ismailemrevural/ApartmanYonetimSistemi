Apartman Yönetim Sistemi
========================

Proje Hakkında
--------------

Bu proje, Java dilinde **Swing** kütüphanesi kullanılarak geliştirilmiş bir **Apartman Yönetim Sistemi**'dir. Sistem, apartman sakinleri ve yöneticilerinin günlük işlemlerini yönetmelerini sağlar. Apartman yönetim sisteminde kullanıcılar, aidat ödemelerini takip edebilir, daire kiralamalarını yönetebilir, bakım talepleri oluşturabilir ve daha birçok yönetimsel işlem gerçekleştirebilir.

Proje, **GUI (Graphical User Interface)** tabanlı bir uygulama olarak geliştirilmiştir ve kullanıcı dostu arayüzü ile işlemleri kolaylaştırmayı amaçlamaktadır.

Özellikler
----------

*   **Kullanıcı Yönetimi:** Apartman sakinlerinin ve yöneticilerinin kaydı yapılabilir.
    
*   **Aidat Yönetimi:** Aidat ödemeleri kaydedilir ve borçlar takip edilir.
    
*   **Daire Yönetimi:** Dairelerin durumu (boş/işgal) takip edilebilir ve kiralama işlemleri yapılabilir.
    
*   **Bakım ve Onarım Talepleri:** Sakinler, bakım talepleri oluşturabilir ve talepler takip edilebilir.
    
*   **Raporlama:** Ödeme geçmişi ve bakım talepleri raporları görüntülenebilir.
    
*   **GUI Arayüzü:** Swing kullanılarak görsel bir kullanıcı arayüzü tasarlanmıştır.
    

Teknolojiler
------------

*   **Java 11** veya daha yeni bir sürüm.
    
*   **Swing:** Java'nın GUI kütüphanesi ile kullanıcı arayüzü tasarımı.
    
*   **IDE:** IntelliJ IDEA, Eclipse, NetBeans gibi Java destekleyen IDE'ler.
    
*   **Veritabanı:** Proje için basit dosya tabanlı bir veri yönetimi kullanılmaktadır. (İleri düzey versiyonlarda veritabanı entegrasyonu yapılabilir.)
    

Başlangıç
---------

### Gereksinimler

1.  **Java Development Kit (JDK):** Java 11 veya daha yeni bir sürüm.
    
2.  **IDE:** Java geliştirme için IntelliJ IDEA, Eclipse veya NetBeans gibi bir IDE önerilir.
    
3.  **Command Line (Opsiyonel):** Java'nın yüklü olduğu terminal veya komut satırı kullanılabilir.
    

### Kurulum

1.  bashCopy codegit clone https://github.com/username/apartman-yonetim-sistemi.git
    
2.  IDE'nizde projeyi açın.
    
3.  Maven veya Gradle kullanıyorsanız bağımlılıkları yüklemek için aşağıdaki komutları kullanabilirsiniz:
    
    *   bashCopy codemvn clean install
        
    *   bashCopy codegradle build
        
4.  Uygulamayı başlatmak için ApartmanYonetimSistemi.java ana sınıfını çalıştırabilirsiniz.
    

### Kullanıcı Arayüzü

Proje, **Swing** kullanılarak geliştirilmiş görsel bir arayüze sahiptir. Kullanıcılar, aşağıdaki menüler ve seçenekler ile sisteme etkileşimde bulunabilirler:

*   **Ana Menü:**
    
    *   **Kullanıcı Kaydı**
        
    *   **Aidat Ödeme**
        
    *   **Daire Durumu Görüntüle**
        
    *   **Bakım Talebi Oluştur**
        
    *   **Çıkış**
        
*   **Kullanıcı Kaydı:** Kullanıcılar, ad, soyad, daire numarası gibi bilgileri girerek sisteme kaydolabilirler.
    
*   **Aidat Ödeme:** Kullanıcılar, ödenmemiş aidatları görüntüleyebilir ve ödeme yapabilirler.
    
*   **Daire Durumu Görüntüle:** Apartmandaki dairelerin kiralanıp kiralanmadığını (boş/işgal) kontrol edebilirsiniz.
    
*   **Bakım Talebi:** Sakinler, dairelerindeki arızalar için bakım talebi oluşturabilirler.
    

### Kullanıcı Arayüzü Örneği

_Örnek Ana Menü_

Uygulama, menüler arasında geçiş yapmayı sağlayan butonlar ve kullanıcıdan veri almak için **JTextField**, **JTextArea**, **JButton** gibi Swing bileşenlerini kullanır.

### Örnek Kullanıcı Akışı

1.  **Kullanıcı Kaydı:** Program başlatıldığında, kullanıcıya kaydolması için bir arayüz sunulur. Kullanıcı, bilgilerini girer ve "Kaydet" butonuna basarak sisteme kaydolur.
    
2.  **Aidat Ödeme:** Kullanıcı, ödeme yapmak için "Aidat Ödeme" menüsüne tıklayarak, mevcut borçları ve ödeme seçeneklerini görüntüler.
    
3.  **Daire Durumu Görüntüleme:** Kullanıcı, dairenin durumunu görmek için "Daire Durumu Görüntüle" seçeneğine tıklayarak dairenin boş olup olmadığını kontrol eder.
    
4.  **Bakım Talebi:** Sakinler, arızalar için bakım talebi oluşturabilir ve taleplerini sisteme kaydedebilir.
    

 

Katkıda Bulunma
---------------

1.  Projeyi GitHub üzerinden **fork**'layın.
    
2.  bashCopy codegit checkout -b feature/özellik-ismi
    
3.  bashCopy codegit commit -am 'Yeni özellik eklendi'
    
4.  bashCopy codegit push origin feature/özellik-ismi
    
5.  Bir **pull request** gönderin.
    

Lisans
------

Bu projede Lisans bulunmamaktadır

İletişim
--------

*   **Geliştirici:** Ali Can Altun
*   **E-posta:** https://github.com/alicanaltun
*   **Geliştirici:** İsmail Emre Vural
*   **E-posta:** https://github.com/ismailemrevural
