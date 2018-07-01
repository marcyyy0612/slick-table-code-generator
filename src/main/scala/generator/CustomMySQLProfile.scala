package generator

import com.typesafe.config.{ Config, ConfigFactory }
import slick.ast.AnonSymbol
import slick.jdbc.meta.{ MColumn, MTable }
import slick.jdbc.{ JdbcModelBuilder, JdbcModelComponent, JdbcProfile, MySQLProfile }

import scala.concurrent.ExecutionContext

object CustomMySQLProfile extends MySQLProfile with CustomJdbcModelComponent {
  def RowNum(sym: AnonSymbol, inc: Boolean) = MySQLProfile.RowNum(sym, inc)

  def RowNumGen(sym: AnonSymbol, init: Long) = MySQLProfile.RowNumGen(sym, init)
}

trait CustomJdbcModelComponent extends JdbcModelComponent {
  self: JdbcProfile =>
  override def createModelBuilder(tables: Seq[MTable], ignoreInvalidDefaults: Boolean)(implicit ec: ExecutionContext): CustomJdbcModelBuilder =
    new CustomJdbcModelBuilder(tables, ignoreInvalidDefaults)
}

class CustomJdbcModelBuilder(mTables: Seq[MTable], ignoreInvalidDefaults: Boolean)(implicit ec: ExecutionContext)
  extends JdbcModelBuilder(mTables, ignoreInvalidDefaults) {

  private[this] val config: Config = ConfigFactory.load
  private[this] val timestampType = config.getString("customType.timestamp")

  override def createColumnBuilder(tableBuilder: TableBuilder, meta: MColumn): ColumnBuilder = new CustomColumnBuilder(tableBuilder, meta)

  class CustomColumnBuilder(tableBuilder: TableBuilder, meta: MColumn) extends ColumnBuilder(tableBuilder, meta) {

    // override def tpe: String =
    //   jdbcTypeToScala(meta.sqlType, meta.typeName).toString match {
    //     case "java.lang.String" => if (meta.size.contains(1)) "Char" else "String"
    //     case "java.sql.Timestamp" =>
    //
    //       // If a [TimeStamp] default value is set such as CURRENT_TIMESTAMP, [Option] is added.
    //       rawDefault match {
    //         case Some(_) if meta.typeName.toUpperCase == "TIMESTAMP" => s"Option[$timestampType]"
    //         case _                                                   => timestampType
    //       }
    //     case "java.sql.Date" => config.getString("customType.date")
    //     case "java.sql.Time" => config.getString("customType.time")
    //     case jdbcType        => jdbcType
    //   }
  }

}
