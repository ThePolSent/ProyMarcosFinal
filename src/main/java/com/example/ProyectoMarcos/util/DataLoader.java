package com.example.ProyectoMarcos.util;

import com.example.ProyectoMarcos.model.Capitulo;
import com.example.ProyectoMarcos.model.Manga;
import com.example.ProyectoMarcos.model.Mangaka;
import com.example.ProyectoMarcos.service.MangaService;
import com.example.ProyectoMarcos.service.MangakaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class DataLoader implements CommandLineRunner {

    private final MangaService mangaService;
    private final MangakaService mangakaService;

    // Constructor para inyectar automáticamente los servicios
    public DataLoader(MangaService mangaService, MangakaService mangakaService) {
        this.mangaService = mangaService;
        this.mangakaService = mangakaService;
    }

    @Override
    public void run(String... args) throws Exception {
        // =================================================================
        // VERIFICACIÓN CLAVE: Verificamos si hay algún Manga guardado.
        // Asumo que si no hay Mangas, la DB está vacía.
        // =================================================================
        if (!mangaService.obtenerTodos().isEmpty()) {
            System.out.println("--- La base de datos ya contiene datos. Omitiendo carga inicial. ---");
            return; // Salir del método run() para evitar duplicación
        }

        System.out.println("--- La base de datos está vacía. Iniciando carga inicial de datos de prueba... ---");
        // =================================================================

        // Mapa para almacenar los objetos Mangaka creados y reutilizarlos (clave: nombre del autor)
        Map<String, Mangaka> mangakasMap = new HashMap<>();


        // --- 1. DATOS DE AUTORES ---

        // Definición de todos los autores (asegurando unicidad)
        Map<String, Mangaka> uniqueMangakasData = new HashMap<>();

        // Autor 1: Masashi Kishimoto (Naruto)
        uniqueMangakasData.put("Masashi Kishimoto", new Mangaka(
                "Masashi Kishimoto", "Japonesa", "08-11-1974", 50, "Naka, Japón",
                "Masashi Kishimoto es un mangaka japonés famoso por crear Naruto, una de las series de manga y anime más reconocidas a nivel mundial. Desde joven, Kishimoto mostró un gran interés por el dibujo y la narración...",
                "https://pm1.aminoapps.com/6724/1bcbd8a03ba2d18b0ecb9912e49535741841911ev2_hq.jpg"));

        // Autor 2: Eiichiro Oda (One Piece)
        uniqueMangakasData.put("Eiichiro Oda", new Mangaka(
                "Eiichiro Oda", "Japonesa", "01-01-1975", 50, "Prefectura de Kumamoto, Japón",
                "Eiichiro Oda comenzó su carrera como mangaka a una edad muy temprana, mostrando desde niño un talento excepcional para el dibujo y la narración de historias...",
                "https://www.cosmobook.pe/wp-content/uploads/2023/09/Eiichiro-Oda-Cosmobook.jpg"));

        // Autor 3: Hajime Isayama (Attack on Titan)
        uniqueMangakasData.put("Hajime Isayama", new Mangaka(
                "Hajime Isayama", "Japonesa", "29-08-1986", 39, "Ōyama, Japón",
                "Hajime Isayama es un mangaka japonés conocido por su obra 'Attack on Titan'. Su fascinación por el dibujo y la creación de mundos post-apocalípticos comenzó desde su infancia.",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTgK2K6M3kBneH7EV2Rdxm8UeBLZGLdKtIm3w&s"));

        // Autor 4: Tsugumi Ohba (Death Note)
        uniqueMangakasData.put("Tsugumi Ohba", new Mangaka(
                "Tsugumi Ohba", "Japonesa", "13-02-1969", 56, "Tokio, Japón",
                "Tsugumi Ohba es el seudónimo de un escritor de manga japonés. Es conocido por ser el autor de la historia de 'Death Note' y 'Bakuman', ambos con gran éxito internacional.",
                "https://covers.openlibrary.org/a/id/14544118-L.jpg"));

        // Autor 5: Hiromu Arakawa (Fullmetal Alchemist)
        uniqueMangakasData.put("Hiromu Arakawa", new Mangaka(
                "Hiromu Arakawa", "Japonesa", "08-05-1973", 52, "Hokkaidō, Japón",
                "Hiromu Arakawa es el seudónimo de una mangaka japonesa conocida por la aclamada serie 'Fullmetal Alchemist'. Su estilo de dibujo es dinámico y expresivo.",
                "https://pm1.aminoapps.com/6630/201aef7a1fa1c020ac20c4b32f20ab375c1e883d_hq.jpg"));

        // Autor 6: Kohei Horikoshi (My Hero Academia)
        uniqueMangakasData.put("Kohei Horikoshi", new Mangaka(
                "Kohei Horikoshi", "Japonesa", "20-11-1986", 38, "Aichi, Japón",
                "Kohei Horikoshi es un mangaka japonés conocido por su trabajo en 'My Hero Academia', que ha ganado gran popularidad a nivel mundial por su vibrante arte y personajes memorables.",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQru-KkZiqxGAUL6HpWCAQndYtZsMa3f3bXfg&s"));

        // Autor 7: Koyoharu Gotouge (Demon Slayer)
        uniqueMangakasData.put("Koyoharu Gotouge", new Mangaka(
                "Koyoharu Gotouge", "Japonesa", "05-05-1989", 36, "Prefectura de Fukuoka, Japón",
                "Koyoharu Gotouge es un mangaka japonés conocido por su obra 'Demon Slayer', que ha logrado un éxito masivo en todo el mundo. Su identidad se mantiene en privado, pero su estilo de dibujo es inconfundible.",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSuppMrU01LXEY6JLeGyxe9v6UKEae4Mu1kcg&s"));

        // Autor 8: Gege Akutami (Jujutsu Kaisen)
        uniqueMangakasData.put("Gege Akutami", new Mangaka(
                "Gege Akutami", "Japonesa", "26-02-1992", 33, "Iwate, Japón",
                "Gege Akutami es el seudónimo de un mangaka japonés. Es mejor conocido por la creación de 'Jujutsu Kaisen'. Su estilo artístico oscuro y sus complejas tramas lo han convertido en uno de los mangakas más populares de la nueva generación.",
                "https://pbs.twimg.com/media/Fr7ovvhXoAUe1kv.jpg"));

        // Autor 9: Tatsuya Endo (Spy x Family)
        uniqueMangakasData.put("Tatsuya Endo", new Mangaka(
                "Tatsuya Endo", "Japonesa", "23-07-1980", 45, "Prefectura de Ibaraki, Japón",
                "Tatsuya Endo es un mangaka japonés. Es conocido principalmente por la creación de 'Spy x Family', que ha ganado popularidad por su mezcla única de acción, comedia y romance.",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTEZ8hZzlaFYHkPZLidnTU_fX60o0HRiApuqjUcj3Z1gcPRlnH6IDJ-DLs2fVwCgH7De60&usqp=CAU"));

        // Autor 10: Tatsuki Fujimoto (Chainsaw Man)
        uniqueMangakasData.put("Tatsuki Fujimoto", new Mangaka(
                "Tatsuki Fujimoto", "Japonesa", "10-10-1992", 33, "Prefectura de Akita, Japón",
                "Tatsuki Fujimoto es un mangaka japonés. Es famoso por sus historias impredecibles y su estilo de arte crudo y expresivo, que lo han convertido en uno de los autores más aclamados de la actualidad.",
                "https://assets.mycast.io/actor_images/actor-tatsuki-fujimoto-255737_large.jpg?1628793654"));

        // Autor 11: Hirohiko Araki (Jojo's Bizarre Adventure)
        uniqueMangakasData.put("Hirohiko Araki", new Mangaka(
                "Hirohiko Araki", "Japonesa", "07-06-1960", 65, "Sendai, Japón",
                "Hirohiko Araki es un mangaka japonés conocido por su trabajo en 'Jojo's Bizarre Adventure'. Su estilo de arte único y su creatividad en las batallas lo han convertido en una leyenda en la industria del manga.",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQR7K1phWCJbtbQ7P1TZxIrKhqE1VWA5leLig&s"));

        // Autor 12: Sui Ishida (Tokyo Ghoul)
        uniqueMangakasData.put("Sui Ishida", new Mangaka(
                "Sui Ishida", "Japonesa", "28-12-1986", 38, "Fukuoka, Japón",
                "Sui Ishida es un mangaka japonés. Es conocido por su estilo de arte oscuro y sus tramas psicológicas, que lo han hecho popular en la industria del manga. Su identidad es muy privada.",
                "https://i.redd.it/kuypyqqfpl581.jpg"));

        // Autor 13: Kentaro Miura (Berserk)
        uniqueMangakasData.put("Kentaro Miura", new Mangaka(
                "Kentaro Miura", "Japonesa", "11-07-1966", 59, "Chiba, Japón",
                "Kentaro Miura fue un mangaka japonés, reconocido por su obra maestra 'Berserk'. Su estilo de arte detallado y sus tramas oscuras lo han convertido en una leyenda en la industria del manga.",
                "https://www.lascosasquenoshacenfelices.com/wp-content/uploads/2021/05/kentaro-miura-fallece-berserk-las-cosas-que-nos-hacen-felices.jpg"));

        // Autor 14: ONE (One-Punch Man)
        uniqueMangakasData.put("ONE", new Mangaka(
                "ONE", "Japonesa", "29-10-1986", 38, "Niigata, Japón",
                "ONE es el seudónimo de un mangaka japonés conocido por su trabajo en 'One-Punch Man' y 'Mob Psycho 100'. Su estilo de arte simple y sus complejas tramas lo han convertido en un autor de culto.",
                "https://static.wikia.nocookie.net/onepunchman/images/0/0f/ONE.jpg/revision/latest/scale-to-width/360?cb=20200508011841&path-prefix=es"));

        // Autor 15: Makoto Yukimura (Vinland Saga)
        uniqueMangakasData.put("Makoto Yukimura", new Mangaka(
                "Makoto Yukimura", "Japonesa", "23-11-1976", 48, "Yokohama, Japón",
                "Makoto Yukimura es un mangaka japonés conocido por su trabajo en 'Vinland Saga'. Su estilo de arte detallado y su investigación histórica exhaustiva lo han convertido en un autor de culto.",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS9zzr2uPF2wcMSAYgSX4tLJbr1dlwjVIHCRA&s"));

        // Autor 16: Yoshihiro Togashi (Hunter x Hunter)
        uniqueMangakasData.put("Yoshihiro Togashi", new Mangaka(
                "Yoshihiro Togashi", "Japonesa", "27-04-1966", 59, "Shinjō, Japón",
                "Yoshihiro Togashi es un mangaka japonés. Es conocido por sus obras 'Hunter x Hunter' y 'Yu Yu Hakusho', que han ganado gran popularidad a nivel mundial por sus complejas tramas y personajes.",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT0EN-QqOsVOp0b7gBop2XnWcbBl5RAuCPmSw&s"));

        // Autor 17: Yūki Tabata (Black Clover)
        uniqueMangakasData.put("Yūki Tabata", new Mangaka(
                "Yūki Tabata", "Japonesa", "30-07-1984", 41, "Fukuoka, Japón",
                "Yūki Tabata es un mangaka japonés. Es conocido por su obra 'Black Clover', que ha ganado gran popularidad a nivel mundial por su vibrante arte y personajes memorables.",
                "https://static.wikia.nocookie.net/ansatsukyoshitsu/images/b/be/Yusei_Matsui.jpg/revision/latest?cb=20180617091219&path-prefix=es"));

        // Autor 18: Kaiu Shirai (The Promised Neverland)
        uniqueMangakasData.put("Kaiu Shirai", new Mangaka(
                "Kaiu Shirai", "Japonesa", "23-01-1988", 37, "Tokio, Japón",
                "Kaiu Shirai es un escritor de manga japonés. Es conocido por la aclamada serie de misterio y fantasía oscura 'The Promised Neverland'.",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSqI_dFckBqxtC1i-hATIRNz8lY_lfKNnTIBITf52ojvyAa1A2zKw4bUMCFgTi2y70j4C4&usqp=CAU"));

        // Autor 19: Riichiro Inagaki (Dr. Stone)
        uniqueMangakasData.put("Riichiro Inagaki", new Mangaka(
                "Riichiro Inagaki", "Japonesa", "23-06-1976", 49, "Tokio, Japón",
                "Riichiro Inagaki es un mangaka japonés, conocido por su trabajo en 'Eyeshield 21' y 'Dr. Stone'.",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ8k6Nn_c8AB9GKwzHn9VbpL0_nv530CpT5ew&s"));

        // Autor 20: Yusei Matsui (Assassination Classroom)
        uniqueMangakasData.put("Yusei Matsui", new Mangaka(
                "Yusei Matsui", "Japonesa", "31-01-1979", 46, "Saitama, Japón",
                "Yusei Matsui es un mangaka japonés. Es conocido por su trabajo en 'Assassination Classroom', que ha ganado gran popularidad a nivel mundial por su mezcla de comedia y acción.",
                "https://static.wikia.nocookie.net/ansatsukyoshitsu/images/b/be/Yusei_Matsui.jpg/revision/latest?cb=20180617091219&path-prefix=es"));


        // --- 2. ASIGNAR OBRAS Y GUARDAR AUTORES ---

        // Asignar obras a los autores (solo las que aparecen en el JSON)
        uniqueMangakasData.get("Masashi Kishimoto").setObras(Arrays.asList("Naruto", "Naruto Shippuden", "Boruto: Naruto Next Generations"));
        uniqueMangakasData.get("Eiichiro Oda").setObras(Arrays.asList("One Piece", "Wanted!"));
        uniqueMangakasData.get("Hajime Isayama").setObras(Arrays.asList("Attack on Titan", "Heart Break One"));
        uniqueMangakasData.get("Tsugumi Ohba").setObras(Arrays.asList("Death Note", "Bakuman"));
        uniqueMangakasData.get("Hiromu Arakawa").setObras(Arrays.asList("Fullmetal Alchemist", "Silver Spoon"));
        uniqueMangakasData.get("Kohei Horikoshi").setObras(Arrays.asList("My Hero Academia", "Oumagadoki Doubutsuen"));
        uniqueMangakasData.get("Koyoharu Gotouge").setObras(Arrays.asList("Demon Slayer: Kimetsu no Yaiba"));
        uniqueMangakasData.get("Gege Akutami").setObras(Arrays.asList("Jujutsu Kaisen"));
        uniqueMangakasData.get("Tatsuya Endo").setObras(Arrays.asList("Spy x Family", "Tista"));
        uniqueMangakasData.get("Tatsuki Fujimoto").setObras(Arrays.asList("Chainsaw Man", "Fire Punch"));
        uniqueMangakasData.get("Hirohiko Araki").setObras(Arrays.asList("Jojo's Bizarre Adventure"));
        uniqueMangakasData.get("Sui Ishida").setObras(Arrays.asList("Tokyo Ghoul", "Choujin X"));
        uniqueMangakasData.get("Kentaro Miura").setObras(Arrays.asList("Berserk"));
        uniqueMangakasData.get("ONE").setObras(Arrays.asList("One-Punch Man", "Mob Psycho 100"));
        uniqueMangakasData.get("Makoto Yukimura").setObras(Arrays.asList("Vinland Saga", "Planetes"));
        uniqueMangakasData.get("Yoshihiro Togashi").setObras(Arrays.asList("Hunter x Hunter", "Yu Yu Hakusho"));
        uniqueMangakasData.get("Yūki Tabata").setObras(Arrays.asList("Black Clover"));
        uniqueMangakasData.get("Kaiu Shirai").setObras(Arrays.asList("The Promised Neverland"));
        uniqueMangakasData.get("Riichiro Inagaki").setObras(Arrays.asList("Dr. Stone", "Eyeshield 21"));
        uniqueMangakasData.get("Yusei Matsui").setObras(Arrays.asList("Assassination Classroom"));

        // Guardar cada Mangaka y almacenar la referencia guardada en el mapa
        for (Map.Entry<String, Mangaka> entry : uniqueMangakasData.entrySet()) {
            // FIX: Cambiado .save a .guardarMangaka
            Mangaka savedMangaka = mangakaService.guardarMangaka(entry.getValue());
            mangakasMap.put(entry.getKey(), savedMangaka);
        }

        // --- 3. CREAR Y GUARDAR MANGAS (usando los autores guardados) ---

        // La fecha de lanzamiento de los capítulos se establece desde LocalDate.now()
        LocalDate hoy = LocalDate.now();

        // 1. Naruto
        Mangaka kishimoto = mangakasMap.get("Masashi Kishimoto");
        Manga naruto = new Manga(
                "Naruto", "Shonen", 5.00,
                "https://upload.wikimedia.org/wikipedia/en/9/94/NarutoCoverTankobon1.jpg",
                "Naruto Uzumaki es un joven ninja que busca reconocimiento y sueña con convertirse en Hokage.",
                true, "¡NUEVO CAPÍTULO DISPONIBLE!",
                "https://naruto-official.com/common/ogp/NTOS_OG-main.png",
                kishimoto // Autor
        );
        Capitulo narutoCap1 = new Capitulo("Capítulo 1: El Zorro de Nueve Colas", 1, "/mangas/naruto/capitulo1.pdf", hoy, naruto);
        Capitulo narutoCap2 = new Capitulo("Capítulo 2: Mi nombre es Konohamaru", 2, "/mangas/naruto/capitulo2.pdf", hoy.plusDays(7), naruto);
        naruto.setCapitulos(Arrays.asList(narutoCap1, narutoCap2));
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(naruto);

        // 2. One Piece
        Mangaka oda = mangakasMap.get("Eiichiro Oda");
        Manga onePiece = new Manga(
                "One Piece", "Shonen", 6.50,
                "https://external-preview.redd.it/A45ODbzXJGXinSKqELTkP5LbwYgAHygC652TN7cSFFQ.png?width=640&crop=smart&auto=webp&s=f792f75e09574902ac572d302de1c3b242a3556a",
                "Monkey D. Luffy surca los mares en busca del legendario tesoro One Piece y sueña con ser el Rey de los Piratas.",
                true, "¡NUEVO CAPÍTULO DISPONIBLE!",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQDhPnoZwWUG4f_d3Ft4K8Y_3sQgTAUT-4LnA&s",
                oda
        );
        Capitulo opCap1 = new Capitulo("Capítulo 1: Romance Dawn", 1, "/mangas/one_piece/capitulo1.pdf", hoy, onePiece);
        onePiece.setCapitulos(Arrays.asList(opCap1));
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(onePiece);

        // 3. Attack on Titan
        Mangaka isayama = mangakasMap.get("Hajime Isayama");
        Manga aot = new Manga(
                "Attack on Titan", "Fantástico", 8.00,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQLf82J5fzWON14kSQOJ0hnEp6rGN275haimg&s",
                "La humanidad vive dentro de ciudades rodeadas por enormes muros, para protegerse de los gigantes humanoides devoradores de personas llamados titanes.",
                false, null, null,
                isayama
        );
        Capitulo aotCap1 = new Capitulo("Capítulo 1: A ti, 2000 años en el futuro", 1, "/mangas/aot/capitulo1.pdf", hoy, aot);
        aot.setCapitulos(Arrays.asList(aotCap1));
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(aot);

        // 4. Death Note
        Mangaka ohba = mangakasMap.get("Tsugumi Ohba");
        Manga deathNote = new Manga(
                "Death Note", "Sobrenatural", 7.50,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQRKBkN7hnLX6vNjWUcwDteiUWv1xrNkuvpTg&s",
                "Light Yagami, un estudiante de bachillerato, encuentra un cuaderno sobrenatural que permite matar a las personas escribiendo sus nombres.",
                true, "El manga de misterio que te hará dudar de la justicia. ¡Un clásico atemporal!",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQhQ-mLhJ8KaHJzg6EXjJ1r91ZvKEG-bIvWUg&s",
                ohba
        );
        Capitulo dnCap1 = new Capitulo("Capítulo 1: Aburrimiento", 1, "/mangas/death_note/capitulo1.pdf", hoy, deathNote);
        deathNote.setCapitulos(Arrays.asList(dnCap1));
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(deathNote);

        // 5. Fullmetal Alchemist
        Mangaka arakawa = mangakasMap.get("Hiromu Arakawa");
        Manga fma = new Manga(
                "Fullmetal Alchemist", "Fantasía", 6.80,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSqkte18AAnfpUw4JkByTJ-gi6oF-cz3cVp3g&s",
                "Dos hermanos, Edward y Alphonse Elric, buscan la Piedra Filosofal para recuperar sus cuerpos después de un fallido intento de revivir a su madre usando la alquimia.",
                false, null, null,
                arakawa
        );
        fma.setCapitulos(new ArrayList<>()); // Sin capítulos iniciales
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(fma);

        // 6. My Hero Academia
        Mangaka horikoshi = mangakasMap.get("Kohei Horikoshi");
        Manga mha = new Manga(
                "My Hero Academia", "Shonen", 6.00,
                "https://m.media-amazon.com/images/I/81ZNkhqRvVL._UF1000,1000_QL80_.jpg",
                "En un mundo donde la mayoría de la población nace con superpoderes, Izuku Midoriya, un joven sin 'Quirk', sueña con convertirse en un héroe.",
                true, "¡Descubre el nuevo arco de la historia y el enfrentamiento final!",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTUMntjCSstBdq0b9AaEUWsvAerlb2E7XP3JA&s",
                horikoshi
        );
        mha.setCapitulos(new ArrayList<>()); // Sin capítulos iniciales
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(mha);

        // 7. Demon Slayer
        Mangaka gotouge = mangakasMap.get("Koyoharu Gotouge");
        Manga ds = new Manga(
                "Demon Slayer", "Acción", 7.20,
                "https://images.cdn2.buscalibre.com/fit-in/360x360/e4/f5/e4f581839b205902167c1f97e27d23d9.jpg",
                "Tanjiro Kamado se convierte en un cazador de demonios para vengar a su familia y curar a su hermana, Nezuko, convertida en demonio.",
                false, null, null,
                gotouge
        );
        ds.setCapitulos(new ArrayList<>()); // Sin capítulos iniciales
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(ds);

        // 8. Jujutsu Kaisen
        Mangaka akutami = mangakasMap.get("Gege Akutami");
        Manga jjk = new Manga(
                "Jujutsu Kaisen", "Shonen", 7.80,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTYdBt-2JSwL_WkH8Jsk2os2LXmHmHf_gGZwg&s",
                "Yuji Itadori se une a una organización secreta de hechiceros de Jujutsu para acabar con una maldición poderosa.",
                true, "¡Descubre la nueva temporada del anime y el próximo arco del manga!",
                "https://wallpapers.com/images/hd/jujutsu-kaisen-4k-fan-art-poster-097r2cgl542i5eez.jpg",
                akutami
        );
        jjk.setCapitulos(new ArrayList<>()); // Sin capítulos iniciales
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(jjk);

        // 9. Spy x Family
        Mangaka endo = mangakasMap.get("Tatsuya Endo");
        Manga sxf = new Manga(
                "Spy x Family", "Comedia", 6.90,
                "https://i5.walmartimages.com/seo/Spy-X-Family-Family-Portrait-Paperback-9781974739066_50fd6f4b-b002-4c3d-a98c-48fdfdfe6f8f.28dd135f6258d366286062da7e7dbc33.jpeg",
                "Un espía, una asesina y una telépata se unen en una familia falsa para llevar a cabo una misión secreta.",
                true, "La familia Forger regresa con nuevas misiones y más risas. ¡No te lo pierdas!",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSasoi8MWN5BzKG0UjD5Osp7jthOeQuY4ha0A&s",
                endo
        );
        sxf.setCapitulos(new ArrayList<>()); // Sin capítulos iniciales
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(sxf);

        // 10. Chainsaw Man
        Mangaka fujimoto = mangakasMap.get("Tatsuki Fujimoto");
        Manga csm = new Manga(
                "Chainsaw Man", "Acción", 8.50,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRD7C-VBmVbGKADwj31GyuWxvmHrqBfm-RycA&s",
                "Denji, un joven empobrecido, se fusiona con su perro-demonio Pochita y se convierte en el 'Chainsaw Man'.",
                false, null, null,
                fujimoto
        );
        csm.setCapitulos(new ArrayList<>()); // Sin capítulos iniciales
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(csm);

        // 11. Jojo's Bizarre Adventure
        Mangaka araki = mangakasMap.get("Hirohiko Araki");
        Manga jojo = new Manga(
                "Jojo's Bizarre Adventure", "Aventura", 7.00,
                "https://i.pinimg.com/736x/a1/ef/df/a1efdfb506907d8966cb62adaee6984a.jpg",
                "La historia de la familia Joestar y su lucha contra el mal, a través de varias generaciones.",
                true, "¡Descubre la nueva parte de la saga!",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQcq6WidmAqmHpzEBUuVR7nSNBr9hEqUCKx4w&s",
                araki
        );
        jojo.setCapitulos(new ArrayList<>()); // Sin capítulos iniciales
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(jojo);

        // 12. Tokyo Ghoul
        Mangaka ishida = mangakasMap.get("Sui Ishida");
        Manga tg = new Manga(
                "Tokyo Ghoul", "Sobrenatural", 7.50,
                "https://cdn.kobo.com/book-images/3868aae5-b13e-4076-8654-75ca0c3a82ca/353/569/90/False/tokyo-ghoul-re-vol-7.jpg",
                "Ken Kaneki, un joven universitario, es convertido en un híbrido entre humano y ghoul, una criatura que se alimenta de carne humana.",
                false, null, null,
                ishida
        );
        tg.setCapitulos(new ArrayList<>()); // Sin capítulos iniciales
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(tg);

        // 13. Berserk
        Mangaka miura = mangakasMap.get("Kentaro Miura");
        Manga berserk = new Manga(
                "Berserk", "Fantasía", 9.00,
                "https://www.nippon.com/es/ncommon/contents/japan-topics/1261990/1261990.jpg",
                "Guts, un espadachín solitario, lucha contra demonios y monstruos para vengarse de su antiguo líder.",
                false, null, null,
                miura
        );
        berserk.setCapitulos(new ArrayList<>()); // Sin capítulos iniciales
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(berserk);

        // 14. One-Punch Man
        Mangaka one = mangakasMap.get("ONE");
        Manga opm = new Manga(
                "One-Punch Man", "Acción", 7.50,
                "https://images.cdn2.buscalibre.com/fit-in/360x360/5c/35/5c3505d928784d5671b06e18ca6e417b.jpg",
                "Saitama, un héroe que puede derrotar a cualquier enemigo de un solo golpe, lucha contra el aburrimiento y la falta de desafíos.",
                true, "El héroe más poderoso regresa. ¡La nueva temporada está a la vuelta de la esquina!",
                "https://static.wikia.nocookie.net/onepunchman/images/0/0f/ONE.jpg/revision/latest/scale-to-width/360?cb=20200508011841&path-prefix=es",
                one
        );
        opm.setCapitulos(new ArrayList<>()); // Sin capítulos iniciales
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(opm);

        // 15. Vinland Saga
        Mangaka yukimura = mangakasMap.get("Makoto Yukimura");
        Manga vs = new Manga(
                "Vinland Saga", "Otros", 8.00,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRFoulhOKGMGx-gTBtn5JWKaWS2mhuhZ7q5sg&s",
                "La historia de un joven vikingo, Thorfinn, que busca la venganza por la muerte de su padre.",
                false, null, null,
                yukimura
        );
        vs.setCapitulos(new ArrayList<>()); // Sin capítulos iniciales
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(vs);

        // 16. Hunter x Hunter
        Mangaka togashi = mangakasMap.get("Yoshihiro Togashi");
        Manga hxh = new Manga(
                "Hunter x Hunter", "Aventura", 7.50,
                "https://jumpg-assets.tokyo-cdn.com/secure/title/100015/title_thumbnail_portrait_list/314380.jpg?hash=MKFL2hpxoFsBEp_KgADJpg&expires=2145884400",
                "Gon Freecss, un joven de 12 años, decide convertirse en un cazador para encontrar a su padre.",
                false, null, null,
                togashi
        );
        hxh.setCapitulos(new ArrayList<>()); // Sin capítulos iniciales
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(hxh);

        // 17. Black Clover
        Mangaka tabata = mangakasMap.get("Yūki Tabata");
        Manga bc = new Manga(
                "Black Clover", "Shonen", 6.00,
                "https://jumpg-assets.tokyo-cdn.com/secure/title/200020/title_thumbnail_portrait_list/311770.jpg?hash=y0NFirx5CJsIQ-S7rE-78g&expires=2145884400",
                "Asta, un joven sin magia en un mundo donde todos la poseen, sueña con convertirse en el Rey Mago.",
                true, "¡El nuevo arco de la historia se acerca! ¡El destino del Reino del Trébol está en juego!",
                "https://wallpapers.com/images/hd/asta-black-clover-4k-anime-season-2-poster-34g3y6bkultf12t9.jpg",
                tabata
        );
        bc.setCapitulos(new ArrayList<>()); // Sin capítulos iniciales
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(bc);

        // 18. The Promised Neverland
        Mangaka shirai = mangakasMap.get("Kaiu Shirai");
        Manga tpn = new Manga(
                "The Promised Neverland", "Drama", 7.50,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRLup6hDUm1rLdsJecmAd2lX8vDOW-rwMoGJw&s",
                "Un grupo de huérfanos descubre la oscura verdad sobre su orfanato y planea escapar.",
                false, null, null,
                shirai
        );
        tpn.setCapitulos(new ArrayList<>()); // Sin capítulos iniciales
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(tpn);

        // 19. Dr. Stone
        Mangaka inagaki = mangakasMap.get("Riichiro Inagaki");
        Manga ds_manga = new Manga(
                "Dr. Stone", "Aventura", 7.00,
                "https://images.cdn1.buscalibre.com/fit-in/360x360/d1/da/d1da4a20a5ded4da0cb8fed14206639b.jpg",
                "Después de 3.700 años de petrificación, el genio Senku Ishigami despierta en un mundo de piedra y se propone restaurar la civilización con la ciencia.",
                true, "¡El viaje para revivir a la humanidad continúa!",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRjHpfDKcaIf9hkHjHPyi03uAbTA8auKgT2Rg&s",
                inagaki
        );
        ds_manga.setCapitulos(new ArrayList<>()); // Sin capítulos iniciales
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(ds_manga);

        // 20. Assassination Classroom
        Mangaka matsui = mangakasMap.get("Yusei Matsui");
        Manga ac = new Manga(
                "Assassination Classroom", "Comedia", 6.80,
                "https://cdn.kobo.com/book-images/d05b2324-4b83-487f-83d4-8fce0e17389b/1200/1200/False/assassination-classroom-vol-1.jpg",
                "Un misterioso y poderoso ser amenaza con destruir la Tierra, y un aula de estudiantes se encarga de asesinarlo.",
                false, null, null,
                matsui
        );
        ac.setCapitulos(new ArrayList<>()); // Sin capítulos iniciales
        // FIX: Cambiado .save a .guardarManga
        mangaService.guardarManga(ac);

        System.out.println("--- Datos iniciales de 20 Mangas cargados en MySQL exitosamente. ---");
    }
}
