package Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TrackRate
{
    /**
     * Identification number
     */
    @Id
    @GeneratedValue
    int id;
    /**
     * Content of single request from device. Data model JSON.
     */
    String content;
}
