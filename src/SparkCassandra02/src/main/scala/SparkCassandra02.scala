import org.apache.spark.{SparkConf, SparkContext}
import com.datastax.spark.connector._

object SparkCassandra02 extends App {
  val conf      = new SparkConf().setMaster("local[*]").setAppName("SparkCassandra02").set("spark.cassandra.connection.host", "127.0.0.1")
  val sc        = new SparkContext(conf)

  // Parte 1 - Coletar os users com mais seguidores está OK;
  val item1     = sc.cassandraTable("twitter", "users").where("partitionkey = ?", 1).collect()
  val item2     = item1.reverse.take(5)
  val top5      = sc.parallelize(item2).persist()
  top5.saveToCassandra("twitter", "users_summary", SomeColumns("partitionkey", "user_id", "name", "followers_count"))

  // Parte 2 - Contabilizar a soma das tags monitoradas para a lang = pt
  val taglist   = sc.textFile("/usr/local/twittargl/files/tag.list")
  taglist.collect().foreach(htag => {
    println("Buscando tag [" + htag + "]... ")
    val item3 = sc.cassandraTable("twitter", "tweets_hashtags").select("hashtag").where("partitionkey = ?", 1).where("lang = ?", "pt").where("hashtag = ?", htag).collect()
    val item4 = sc.parallelize(item3)
    val qtde  = item4.count()
    println("[" + qtde + "] tweets com a hashtag #" + htag + ".\n")
    val collection = sc.parallelize(Seq((1, htag, qtde)))
    collection.saveToCassandra("twitter", "hashtags_summary", SomeColumns("partitionkey", "hashtag", "count"))
  })

  // Parte 3 - Contabilizar todos os tweets por hora.
  val hrs_dia = 0 to 23
  hrs_dia.foreach(hora => {
    val hora_dia   = "%02d".format(hora)
    val format_in  = new java.text.SimpleDateFormat("yyyy-MM-dd " + hora_dia + ":00:00+0000")
    val format_out = new java.text.SimpleDateFormat("yyyy-MM-dd " + hora_dia + ":59:59+0000")
    val date_from  = format_in.format(new java.util.Date())
    val date_to    = format_out.format(new java.util.Date())
    println("Buscando período [" + date_from + "/" + date_to + "]... ")
    val item5 = sc.cassandraTable("twitter", "tweets").select("id_str").where("timestamp_ms >= ?", date_from).where("timestamp_ms <= ?", date_to).collect()
    val item6 = sc.parallelize(item5)
    val qtdet = item6.count()
    val tmp1  = sc.parallelize(Seq((1, hora_dia, qtdet)))
    tmp1.saveToCassandra("twitter", "tweets_summary", SomeColumns("partitionkey", "hora_dia", "count"))
  })

  sc.stop()
}