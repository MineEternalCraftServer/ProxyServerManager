package server.mecs.proxyservermanager

import com.fasterxml.jackson.databind.ObjectMapper
import server.mecs.proxyservermanager.database.MongoDBManager

class getIDfromMCID {
    val id: Long? = null
    fun getIDfromMCID(con: MongoDBManager, mcid: String): Long? {
        val result = con.queryFind("{mcid:'${mcid}'}").toString()
        val mapper = ObjectMapper()
        val convertJson = mapper.readValue(result, getIDfromMCID::class.java)
        return convertJson.id
    }
}