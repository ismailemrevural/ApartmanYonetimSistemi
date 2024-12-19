# Apartman Yönetim Sistemi

Bu proje, apartman yönetimi için geliştirilmiş ve Java dilinde yazılmış bir masaüstü uygulamasıdır. Özellikle apartman yöneticileri için birçok fonksiyonellik barındırmakta ve yönetimi kolaylaştırmaktadır. *Swing* kütüphanesi kullanılarak GUI oluşturulmuştur.

## Özellikler

### Yönetici Yetkileri

- *Kullanıcı Yönetimi*:
  - Kullanıcı ekleme, silme ve yetki verme işlemleri.
- *Aidat Yönetimi*:
  - Aidat ödemelerini takip etme ve raporlama.
- *Bakım/Onarım Yönetimi*:
  - Bakım ve onarım kayıtlarını ekleme, silme ve listeleme.
- *Bildirim Yönetimi*:
  - Kullanıcılara bildirim gönderme.
- *Finansal Raporlama*:
  - Gelir ve giderleri raporlama.

### Kullanıcı Yetkileri

- *Kullanıcı İşlemleri*:
  - Kullanıcı kayıt olma, giriş yapma.
- *Aidat Görüntüleme*:
  - Kullanıcının sistemdeki kendisine ait olan aidat kayıtlarını görüntülemesi.
- *Bakım/Onarım Görüntüleme*:
  - Kullanıcının sistemdeki bakım ve onarım kayıtlarını görüntülemesi.
- *Bildirim Görüntüleme*:
  - Kullanıcının kendisine gönderilmiş bildirimleri görüntülemesi.
- *Finansal Rapor Görüntüleme*:
  - Kullanıcının apartmanın gelir ve giderlerini şeffaf şekilde görüntülemesi.

## Kurulum

1. *Java JDK* ve *IntelliJ IDEA* yüklü olduğundan emin olun.
2. Projeyi *IntelliJ IDEA*’da çalıştırın.
3. “apartmanyonetimsistemi.sql” dosyasını MySQL veritabanında çalıştırın.
4. MySQL giriş bilgilerinizi src /Database.java dosyasında düzenleyin.
5. Eğer projede eksik kütüphane varsa, aşağıdaki bağımlılıkları ekleyin:
   - mysql-connector-j-9.1.0
   - jcalendar-1.4

## Kullanım

1. Projeyi IntelliJ IDEA'da çalıştırın.
2. İlk kez kullanıyorsanız, apartman bilgilerini girmeniz istenecektir.
3. Giriş sayfasında, ilk kullanıcı yönetici olarak oluşturulur. Daha sonra oluşturulan kullanıcılar sakin olarak sisteme eklenir.
4. Yönetici hesabıyla giriş yaptıktan sonra, diğer kullanıcıları yönetici olarak yetkilendirebilirsiniz.
5. Giriş yaptıktan sonra, kullanıcı rolünüze uygun işlemleri gerçekleştirebilirsiniz.

## Katkıda Bulunma

Katkıda bulunmak isterseniz, lütfen bir *pull request* gönderin veya bir *issue* açın.

## Lisans

Bu proje şu an için lisanssızdır.

## Geliştiriciler

- *Ali Can Altun*
  - [GitHub Profili](https://github.com/alicanaltun)
- *İsmail Emre Vural*
  - [GitHub Profili](https://github.com/ismailemrevural)
