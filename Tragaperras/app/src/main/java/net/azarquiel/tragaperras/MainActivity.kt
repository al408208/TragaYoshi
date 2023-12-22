package net.azarquiel.tragaperras

import android.graphics.drawable.AnimationDrawable
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.content_main.*
import net.azarquiel.dadosgame.model.Ficha
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jetbrains.anko.yesButton

class MainActivity : AppCompatActivity(), View.OnClickListener{

    val anis = intArrayOf(R.drawable.ani1, R.drawable.ani2, R.drawable.ani3)
    var sp: SoundPool? = null
    var sonido: Int = 0
    var sonidoStream: Int = 0
    val frames = arrayOfNulls<AnimationDrawable>(3)
    lateinit var ivs:Array<ImageView>

    val arrayJugada = IntArray(3) {0}
    val figura = arrayOf("campana", "cereza", "dolar", "limon", "fresa", "siete")
    private var parando: Boolean = false
    private var dollars=20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadResources()
        iv4.setOnClickListener{tirar()}
    }

    /**
     * Load Resources
     */

    private fun loadResources() {
        loadSound()
        loadAnimations()
    }

    private fun loadSound() {
        // load sonido
        sp = SoundPool(5, AudioManager.STREAM_MUSIC, 0)
        sonido = sp!!.load(this, R.raw.maquina, 1)
    }

    private fun loadAnimations() {
        ivs = arrayOf(iv1,iv2,iv3)
        for ((i,iv) in ivs.withIndex()){
            iv.setOnClickListener(this)
            iv.setImageResource(android.R.color.transparent)
            iv.setBackgroundResource(anis[i])
            frames[i] = iv.background as AnimationDrawable
            iv.tag = Ficha(figura[i])
            arrayJugada[i] = i

        }
    }

    override fun onClick(v: View?) {
        if (parando) return
        val botonJugar = v as ImageView
        if(botonJugar.id== R.id.iv4 && dollars>=20){
            tirar()
        }

    }

    private fun tirar(){
        if (parando) return
        sonidoStream = sp!!.play(sonido, 1f, 1f, 1, -1, 1f)
        for ((i,iv) in ivs.withIndex()){
            iv.setImageResource(android.R.color.transparent)
            frames[i]!!.start()
            parando=true
            parar(i)
        }
        //parando=true   V1
        //parar()
    }

    private fun parar(i:Int){// V1 private fun parar()
        doAsync {
            var result = duerme(1000*(i+1),i)//V1  SystemClock.sleep(1000)
            uiThread {
                // V1 for ((i,iv) in ivs.withIndex()){
                    //ponCara(iv,i)
                //}
                //sp!!.stop(sonidoStream)
                //parando=false
                //dinero()
                ponCara(result)
                if(i==2){     //este if es V2
                    sp!!.stop(sonidoStream)
                    dinero()
                    parando=false
                }
            }
        }
    }

    private fun ponCara( i: Int) {//  V1 private fun ponCara(iv: ImageView, i: Int) {
        val ficha = ivs[i].tag as Ficha//V1 poner iv, no array ivs
        val n = (Math.random()*6).toInt()
        val id = resources.getIdentifier("${figura[n]}","drawable",packageName)
        ivs[i].setImageResource(id)
        ficha.valor = figura[n]
        ivs[i].tag = ficha
        arrayJugada[i] = n
        frames[i]!!.stop()
    }
    //V2
    private fun duerme(time:Int, i:Int): Int {
        SystemClock.sleep(time.toLong())
        return i
    }

    private fun dinero(){
        if((arrayJugada[0]==5)&&(arrayJugada[1]==5)&&(arrayJugada[2]==5)){
            dollars+=50
        }else if((arrayJugada[0]==arrayJugada[1])&&(arrayJugada[0]==arrayJugada[2])){
            dollars+=20
        }else{
            dollars-=1
        }
        textView.setText(dollars.toString())
        fin()
    }

    private fun fin(){
        if(dollars>=50){
            alert("Has conseguido ${dollars} YC y has reventado la maquina") {
                title=("Enhorabuena")
                yesButton {  }
            }.show()
            iv4.isEnabled=false
        }else if(dollars==0){
            alert("Te has quedadod sin Yoshi coins") {
                title=("Mala suerte")
                yesButton {  }
            }.show()
            iv4.isEnabled=false
        }
    }
}

