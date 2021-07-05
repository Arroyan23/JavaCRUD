package com.tutorial;
import java.io.File;
import java.io.*;
import java.time.*;
import java.util.*;
public class Main {
    public static void main(String[] args) throws IOException {
        //membuat project CRUD
        //main menu
        //scanner
        Scanner inputOptions = new Scanner(System.in);
        boolean isLanjutkan = true;
        //list
        while(isLanjutkan) {
            clearScreen();
            System.out.println("Selamat datang di databse perpusatakaan");
            System.out.println("1. Lihat seluruh buku");
            System.out.println("2. Cari data buku");
            System.out.println("3. Tambah data buku");
            System.out.println("4. Ubah data buku");
            System.out.println("5. Hapus data buku");

            System.out.print("masukkan list anda = ");
            String input1 = inputOptions.next();

            switch (input1) {
                case "1":
                    System.out.println("\n==================");
                    System.out.println("Lihat seluruh buku");
                    System.out.println("==================");
                    tampilkanBuku();
                    break;
                case "2":
                    System.out.println("\n==============");
                    System.out.println("Cari data buku");
                    System.out.println("==============");
                    cariBuku();
                    break;
                case "3":
                    System.out.println("\n================");
                    System.out.println("Tambah data buku");
                    System.out.println("================");
                    tambahData();
                    tampilkanBuku();
                    break;
                case "4":
                    System.out.println("\n==============");
                    System.out.println("Ubah data buku");
                    System.out.println("==============");
                    break;
                case "5":
                    System.out.println("\n===============");
                    System.out.println("Hapus data buku");
                    System.out.println("===============");
                    deleteDataBuku();
                    break;
                default:
                    System.out.println("yang anda masukkan adalah bukan list");
                    break;
            }


            isLanjutkan = getYesNo("apakah anda ingin melanjutkan?");
        }
        //melanjutkan nanti
        //menambahkan function looping!
    }

    //fungsi untuk mendelet datavase

    private static void deleteDataBuku() throws IOException{
        //mengambil database original
        File fileDatabase = new File("database.txt");
        FileReader reader = new FileReader(fileDatabase);
        BufferedReader buffRead = new BufferedReader(reader);
        //mengambil database sementara
        File fileData = new File("tempdb.txt");
        FileWriter writer = new FileWriter(fileData);
        BufferedWriter buffW = new BufferedWriter(writer);
        //tampilkan data
        System.out.println("mana data yang ingin anda hapus");
        tampilkanBuku();
        //mengambil data dari user input
        Scanner termInput = new Scanner(System.in);
        System.out.print("Masukkan data yang ingin anda delete: ");
        int delInput = termInput.nextInt();
        //looping untuk mendelete data


        int countEntry = 0; //baris dari setiap data
        String data = buffRead.readLine();

        while(data != null){
            countEntry++;
            boolean isDelete = false; //apakah mau di delete?

            StringTokenizer Token = new StringTokenizer(data , ",");

            if(delInput == countEntry){
                System.out.println("data yang ingin anda hapus adalah sebagai beris berikut: ");
                System.out.println("---------------------------------------------------------");
                System.out.println("Refrensi   : " + Token.nextToken());
                System.out.println("Tahun      : " + Token.nextToken());
                System.out.println("Penulis    : " + Token.nextToken());
                System.out.println("penerbit   : " + Token.nextToken());
                System.out.println("Judul buku : " + Token.nextToken());

                isDelete = getYesNo("apakah anda yakin akan menghapus data ini?");

            }

            if(isDelete){
                //menampilkan data untuk sementara
                System.out.println("data anda berhasil di hapus");
                return;
            }else{
                buffW.write(data);
                buffW.newLine();
            }

            data = buffRead.readLine();
        }
        //menulis data ke tempDB
        buffW.flush();
        //menghapus file original
        fileDatabase.delete();
        //mengganti temp db dengan database.txt
        fileData.renameTo(fileDatabase);
        //ignored file delete
    }

    //fungsi untuk menambahkan data

    private static void tambahData() throws IOException{
        FileWriter FileOutput = new FileWriter("database.txt" , true);
        BufferedWriter bufferedWriter = new BufferedWriter(FileOutput);

        Scanner terminalInput = new Scanner(System.in);
        String judul , tahun , penulis , penerbit;

        System.out.print("masukkan penulis buku: ");
        penulis = terminalInput.nextLine();
        System.out.print("Masukkan judul buku: ");
        judul = terminalInput.nextLine();
        System.out.print("Masukkan penerbit buku: ");
        penerbit = terminalInput.nextLine();
        System.out.print("Masukkan tahun terbit (YYYY) : ");
        tahun = terminalInput.nextLine();

        boolean tahunExist = false;

        while(!tahunExist) {

            try {
                Year.parse(tahun);
                tahunExist = true;
            } catch (Exception e) {
                System.out.println("this is not a year");
                System.out.print("Masukkan tahun terbit lagi (YYYY) : ");
                tahun = terminalInput.nextLine();
            }
        }

        String[] keyword = {tahun + "," + penulis + "," + penerbit + "," + judul};
        System.out.println(Arrays.toString(keyword));

        boolean isExist = databse(keyword , false);

        if(!isExist){
            System.out.println("nomor entrynya adalah = " + ambilNomorEntryperthun(penulis , tahun));
            long nomorEntry = ambilNomorEntryperthun(penulis , tahun);


            String penulisWithoutSpace = penulis.replaceAll("\\s+" , "");
            String sng = penulisWithoutSpace + "_" + tahun + "_" + nomorEntry;

            System.out.println("yang ingin anda masukkan adalah sebagai berikut");
            System.out.println("-----------------------------------------------");
            System.out.println("primary key  : " + sng);
            System.out.println("tahun terbit : " + tahun);
            System.out.println("Penulis      : " + penulis);
            System.out.println("judul        : " + judul);
            System.out.println("penerbit     : " + penerbit);

            boolean pertanyaan = getYesNo("apakah anda ingin menambahkan data tersebut?");
            //menambahkan logika untuk menulis ke outputnya
            if(pertanyaan){
                bufferedWriter.write(sng + "," + tahun + "," + penulis + "," + penerbit + "," + judul);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        }else{
            System.out.println("data anda telah ada sebagai berikut: ");
            databse(keyword , true);
        }

        bufferedWriter.close();
    }

    //fungsi untuk menghitung entry pertahun

    private static long ambilNomorEntryperthun(String penulis , String tahun) throws IOException{
        FileReader fileReader = new FileReader("database.txt");
        BufferedReader buffRead = new BufferedReader(fileReader);

        String data =  buffRead.readLine();
        Scanner userInput;
        String primaryKey;
        long entry = 0;
        while(data != null){
            userInput = new Scanner(data);
            userInput.useDelimiter(",");
            primaryKey = userInput.next();
            userInput = new Scanner(primaryKey);
            userInput.useDelimiter("_");

            penulis = penulis.replaceAll("\\s+" , "");

            if(penulis.equalsIgnoreCase(userInput.next()) && tahun.equalsIgnoreCase(userInput.next())){
                entry = userInput.nextInt();
            }

            data = buffRead.readLine();
        }

        return entry;
    }

    private static void clearScreen(){
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033\143");//untuk menutup di apple
            }
        }catch(Exception ex){
            System.err.println("this is not the matches os");
        }
    }
    //fungsi untuk mencari buku

    private static void cariBuku() throws IOException{
        try{
            File file = new File("database.txt");
        }catch(Exception e){
            System.err.println("Error server not founded");
        }

        Scanner terminalInput = new Scanner(System.in);
        System.out.print("masukkan kata kunci = ");
        String input12 = terminalInput.nextLine();

        String[] keyword = input12.split("\\s+");

        //mencari databse

        databse(keyword , true);
    }

    private static boolean databse(String[] keywords , boolean isDisplay) throws IOException{

        FileReader fileReader = new FileReader("database.txt");
        BufferedReader BufferedReader = new BufferedReader(fileReader);

        String data = BufferedReader.readLine();

        int jumlahData = 0;

        boolean isExist = false;

        if(isDisplay) {
            System.out.println("\n| no | \ttahun  | \tpenulis                  | \tpenerbit         | \tjudul buku");
            System.out.print("----------------------------------------------------------------------------------\n");
        }
        while(data != null){

            isExist = true;

            for(String keyword: keywords) {
                isExist = isExist && data.toLowerCase().contains(keyword.toLowerCase());
            }

            if(isExist){

                if(isDisplay) {
                    jumlahData++;
                    StringTokenizer stringToken = new StringTokenizer(data, ",");
                    stringToken.nextToken();
                    System.out.printf("|  %2d|", jumlahData);
                    System.out.printf("  %-5s  |", stringToken.nextToken());
                    System.out.printf("   %-20s      |", stringToken.nextToken());
                    System.out.printf("  %-14s   |", stringToken.nextToken());
                    System.out.print(stringToken.nextToken());
                    System.out.print("\n");
                }else{
                    break;
                }
            }

            data = BufferedReader.readLine();
        }
        if(isDisplay) {
            System.out.print("----------------------------------------------------------------------------------\n");
        }
        return isExist;
    }

    private static void tampilkanBuku() throws IOException {
        //fungsi untuk membaca data
        //membaca kode
        FileReader FileRead;
        BufferedReader buffRead;
        try {
            FileRead = new FileReader("database.txt");
            buffRead = new BufferedReader(FileRead);
        } catch (Exception ex) {
            System.err.println("your databse is not founded");
            return;
        }


        String data = buffRead.readLine();

        System.out.println("\n| no | \ttahun  | \tpenulis                  | \tpenerbit         | \tjudul buku");
        System.out.print("----------------------------------------------------------------------------------\n");

        int numberPlus = 0;

        while (data != null) {

            numberPlus++;

            StringTokenizer stringToken = new StringTokenizer(data, ",");

            stringToken.nextToken();
            System.out.printf("|  %2d|", numberPlus);
            System.out.printf("  %-5s  |", stringToken.nextToken());
            System.out.printf("   %-20s      |", stringToken.nextToken());
            System.out.printf("  %-14s   |", stringToken.nextToken());
            System.out.print(stringToken.nextToken());
            System.out.print("\n");

            data = buffRead.readLine();
        }
        System.out.print("----------------------------------------------------------------------------------\n");
    }

    private static boolean getYesNo (String pesan){
        Scanner inputGet = new Scanner(System.in);
        System.out.print("\n" + pesan + "(y/n) ");
        String inputMe = inputGet.next();

        while(!inputMe.equalsIgnoreCase("y") && !inputMe.equalsIgnoreCase("n")){
            System.err.println("yang anda masukkan bukanlah pilihan");
            System.out.print(pesan + "(y/n)? ");
            inputMe = inputGet.next();
        }

        return inputMe.equalsIgnoreCase("y");

    }
}
